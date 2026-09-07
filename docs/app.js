// Air Arsenal - Advanced Tactical 3D Model Showcase & Component Inspector
let CATALOG = null;
let currentCategory = null;
let currentItem = null;

// Three.js State
let scene, camera, renderer, controls, modelGroup;
let autoSpin = true;
let wireframeMode = false;
let explodedMode = false;
let tiresRoll = true;

// Animated subcomponents
let animPropeller = null;
let animRotors = [];
let animAfterburners = [];
let animWheels = [];
let steerableWheels = [];
let animTurret = null;
let modelParts = []; // For raycasting & exploded view

// Raycaster for part inspection
const raycaster = new THREE.Raycaster();
const mouse = new THREE.Vector2();
let hoveredMesh = null;
let originalMaterialMap = new Map();

// Initialize 3D Engine
function init3D() {
  const container = document.getElementById('stage-canvas-container');
  if (!container) return;

  const width = container.clientWidth;
  const height = container.clientHeight;

  scene = new THREE.Scene();
  scene.background = new THREE.Color(0x080b0f);
  scene.fog = new THREE.FogExp2(0x080b0f, 0.025);

  camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 100);
  camera.position.set(4.5, 3.2, 5.5);

  renderer = new THREE.WebGLRenderer({ antialias: true, alpha: false, logarithmicDepthBuffer: true });
  renderer.setSize(width, height);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
  renderer.shadowMap.enabled = true;
  renderer.shadowMap.type = THREE.PCFSoftShadowMap;
  container.appendChild(renderer.domElement);

  controls = new THREE.OrbitControls(camera, renderer.domElement);
  controls.enableDamping = true;
  controls.dampingFactor = 0.05;
  controls.maxPolarAngle = Math.PI / 2 + 0.08;
  controls.minDistance = 1.5;
  controls.maxDistance = 25;

  // Grid Floor with subtle glow
  const grid = new THREE.GridHelper(20, 20, 0x38bdf8, 0x1e293b);
  grid.position.y = -1.2;
  scene.add(grid);

  // Lighting Rig
  const hemiLight = new THREE.HemisphereLight(0xffffff, 0x1e293b, 0.9);
  scene.add(hemiLight);

  const mainLight = new THREE.DirectionalLight(0xffffff, 1.4);
  mainLight.position.set(8, 14, 10);
  mainLight.castShadow = true;
  mainLight.shadow.mapSize.width = 1024;
  mainLight.shadow.mapSize.height = 1024;
  scene.add(mainLight);

  const rimLight = new THREE.DirectionalLight(0x38bdf8, 0.8);
  rimLight.position.set(-8, 5, -8);
  scene.add(rimLight);

  const underLight = new THREE.PointLight(0x2563eb, 1.2, 10);
  underLight.position.set(0, -0.8, 0);
  scene.add(underLight);

  modelGroup = new THREE.Group();
  scene.add(modelGroup);

  // Interaction events
  container.addEventListener('mousemove', onMouseMove);
  container.addEventListener('click', onCanvasClick);
  window.addEventListener('resize', onWindowResize);

  buildModelGeometry(currentItem.id);
  renderLoop();
}

function onWindowResize() {
  const container = document.getElementById('stage-canvas-container');
  if (!container || !camera || !renderer) return;
  const width = container.clientWidth;
  const height = container.clientHeight;
  camera.aspect = width / height;
  camera.updateProjectionMatrix();
  renderer.setSize(width, height);
}

function onMouseMove(event) {
  const container = document.getElementById('stage-canvas-container');
  if (!container) return;
  const rect = container.getBoundingClientRect();
  mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1;
  mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1;

  if (!camera || modelParts.length === 0) return;
  raycaster.setFromCamera(mouse, camera);
  const intersects = raycaster.intersectObjects(modelParts, true);

  const hudBox = document.getElementById('part-hud-box');
  const hudName = document.getElementById('part-hud-name');
  const hudSub = document.getElementById('part-hud-subsystem');

  if (intersects.length > 0) {
    let topMesh = intersects[0].object;
    while (topMesh && !topMesh.userData.partName && topMesh.parent && topMesh.parent !== modelGroup) {
      topMesh = topMesh.parent;
    }

    if (topMesh && topMesh.userData.partName) {
      if (hoveredMesh !== topMesh) {
        unhighlightMesh(hoveredMesh);
        hoveredMesh = topMesh;
        highlightMesh(hoveredMesh);
      }
      if (hudName) hudName.innerText = topMesh.userData.partName;
      if (hudSub) hudSub.innerText = (topMesh.userData.subsystem || 'SUBSYSTEM').toUpperCase();
      if (hudBox) hudBox.style.borderColor = '#38bdf8';
      return;
    }
  }

  if (hoveredMesh) {
    unhighlightMesh(hoveredMesh);
    hoveredMesh = null;
    if (hudName) hudName.innerText = 'Hover or click parts to inspect';
    if (hudSub) hudSub.innerText = 'SYSTEM READY';
    if (hudBox) hudBox.style.borderColor = 'var(--border-subtle)';
  }
}

function onCanvasClick() {
  if (hoveredMesh && hoveredMesh.userData.partName) {
    selectPartItemByName(hoveredMesh.userData.partName);
  }
}

function highlightMesh(mesh) {
  if (!mesh) return;
  mesh.traverse(child => {
    if (child.isMesh && child.material) {
      if (!originalMaterialMap.has(child)) {
        originalMaterialMap.set(child, child.material);
      }
      child.material = child.material.clone();
      child.material.emissive = new THREE.Color(0x38bdf8);
      child.material.emissiveIntensity = 0.45;
    }
    if (child.isLineSegments && child.material) {
      if (!originalMaterialMap.has(child)) {
        originalMaterialMap.set(child, child.material);
      }
      child.material = child.material.clone();
      child.material.color = new THREE.Color(0x38bdf8);
      child.material.opacity = 0.95;
    }
  });
}

function unhighlightMesh(mesh) {
  if (!mesh) return;
  mesh.traverse(child => {
    if ((child.isMesh || child.isLineSegments) && originalMaterialMap.has(child)) {
      child.material = originalMaterialMap.get(child);
    }
  });
}

// Render & Animation Loop
let animTime = 0;
function renderLoop() {
  requestAnimationFrame(renderLoop);
  animTime += 0.03;

  if (controls) controls.update();

  // Auto-Spin
  if (autoSpin && modelGroup) {
    modelGroup.rotation.y += 0.007;
  }

  // Spinning Propellers
  if (animPropeller) {
    animPropeller.rotation.z += 0.45;
  }

  // Helicopter Rotors
  animRotors.forEach(r => {
    if (r.userData.isTailRotor) {
      r.rotation.x += 0.55;
    } else {
      r.rotation.y += 0.45;
    }
  });

  // Movable / Rolling Tires
  if (tiresRoll) {
    animWheels.forEach(w => {
      w.rotation.x += 0.08;
    });

    // Steer front wheels smoothly
    const steerAngle = Math.sin(animTime * 0.7) * 0.22;
    steerableWheels.forEach(sw => {
      sw.rotation.y = steerAngle;
    });
  }

  // Afterburner Flame Pulsation
  animAfterburners.forEach(ab => {
    const scaleZ = 0.85 + Math.sin(animTime * 4.0) * 0.3;
    ab.scale.set(1, 1, scaleZ);
  });

  // Tank Turret gentle sweep if enabled
  if (animTurret && autoSpin) {
    animTurret.rotation.y = Math.sin(animTime * 0.5) * 0.35;
  }

  // Smooth Exploded View Lerping
  modelParts.forEach(p => {
    if (p.userData.basePos && p.userData.explodedPos) {
      const target = explodedMode ? p.userData.explodedPos : p.userData.basePos;
      p.position.lerp(target, 0.08);
    }
  });

  if (renderer && scene && camera) {
    renderer.render(scene, camera);
  }
}

function toggleSpin() {
  autoSpin = !autoSpin;
  const btn = document.getElementById('btn-spin');
  if (btn) {
    btn.classList.toggle('active', autoSpin);
    btn.innerText = autoSpin ? 'ROTATE' : 'ROTATE [OFF]';
  }
}

function toggleWireframe() {
  wireframeMode = !wireframeMode;
  const btn = document.getElementById('btn-wireframe');
  if (btn) btn.classList.toggle('active', wireframeMode);
  buildModelGeometry(currentItem.id);
}

function toggleExplodedView() {
  explodedMode = !explodedMode;
  const btn = document.getElementById('btn-exploded');
  if (btn) {
    btn.classList.toggle('active-amber', explodedMode);
    btn.innerText = explodedMode ? 'EXPLODED VIEW [ON]' : 'EXPLODED VIEW [OFF]';
  }
}

function toggleTiresRoll() {
  tiresRoll = !tiresRoll;
  const btn = document.getElementById('btn-tires');
  if (btn) {
    btn.classList.toggle('active', tiresRoll);
    btn.innerText = tiresRoll ? 'TIRES: ROLL [ON]' : 'TIRES: ROLL [OFF]';
  }
}

function resetCamera() {
  if (!camera || !controls || !modelGroup) return;
  camera.position.set(4.5, 3.2, 5.5);
  controls.target.set(0, 0, 0);
  modelGroup.rotation.set(0, 0, 0);
}

// ── Detailed Geometry Generator ─────────────────────────────────────────────
function buildModelGeometry(id) {
  if (!modelGroup) return;

  // Clear previous meshes
  while (modelGroup.children.length > 0) {
    modelGroup.remove(modelGroup.children[0]);
  }
  animPropeller = null;
  animRotors = [];
  animAfterburners = [];
  animWheels = [];
  steerableWheels = [];
  animTurret = null;
  modelParts = [];
  originalMaterialMap.clear();

  // Helper materials
  const makeMat = (color, metal = 0.35, rough = 0.5, opacity = 1.0) => new THREE.MeshStandardMaterial({
    color: color,
    metalness: metal,
    roughness: rough,
    wireframe: wireframeMode,
    transparent: opacity < 1.0,
    opacity: opacity
  });

  // Reusable part creator with CAD line edges & exploded metadata
  const addPart = (name, subsystem, geom, mat, pos, explodedDelta = { x: 0, y: 0, z: 0 }, rot = { x: 0, y: 0, z: 0 }, parent = modelGroup, addEdges = true) => {
    const mesh = new THREE.Mesh(geom, mat);
    mesh.position.set(pos.x, pos.y, pos.z);
    mesh.rotation.set(rot.x, rot.y, rot.z);
    mesh.castShadow = true;
    mesh.receiveShadow = true;

    mesh.userData = {
      partName: name,
      subsystem: subsystem,
      basePos: new THREE.Vector3(pos.x, pos.y, pos.z),
      explodedPos: new THREE.Vector3(pos.x + explodedDelta.x, pos.y + explodedDelta.y, pos.z + explodedDelta.z)
    };

    if (addEdges && !wireframeMode) {
      const threshold = mat.opacity < 0.9 ? 15 : 28;
      const edges = new THREE.EdgesGeometry(geom, threshold);
      const edgeColor = mat.opacity < 0.9 ? 0x7dd3fc : 0x0f172a;
      const edgeLine = new THREE.LineSegments(edges, new THREE.LineBasicMaterial({
        color: edgeColor,
        transparent: true,
        opacity: mat.opacity < 0.9 ? 0.5 : 0.32
      }));
      mesh.add(edgeLine);
    }

    parent.add(mesh);
    modelParts.push(mesh);
    return mesh;
  };

  const addBox = (name, subsystem, w, h, d, color, pos, expDelta = { x: 0, y: 0, z: 0 }, rot = { x: 0, y: 0, z: 0 }, metal = 0.35, rough = 0.5, opacity = 1.0, parent = modelGroup) => {
    return addPart(name, subsystem, new THREE.BoxGeometry(w, h, d), makeMat(color, metal, rough, opacity), pos, expDelta, rot, parent);
  };

  // Cylinders aligned along Z axis (standard longitudinal axis for missiles, aircraft, barrels)
  const addCylZ = (name, subsystem, radiusTop, radiusBottom, length, color, pos, expDelta = { x: 0, y: 0, z: 0 }, segments = 20, metal = 0.4, rough = 0.45, opacity = 1.0, parent = modelGroup) => {
    const geom = new THREE.CylinderGeometry(radiusTop, radiusBottom, length, segments);
    geom.rotateX(Math.PI / 2);
    return addPart(name, subsystem, geom, makeMat(color, metal, rough, opacity), pos, expDelta, { x: 0, y: 0, z: 0 }, parent);
  };

  // Cones aligned forward along Z axis
  const addConeZ = (name, subsystem, radius, length, color, pos, expDelta = { x: 0, y: 0, z: 0 }, segments = 20, metal = 0.4, rough = 0.45, opacity = 1.0, parent = modelGroup) => {
    const geom = new THREE.ConeGeometry(radius, length, segments);
    geom.rotateX(Math.PI / 2);
    return addPart(name, subsystem, geom, makeMat(color, metal, rough, opacity), pos, expDelta, { x: 0, y: 0, z: 0 }, parent);
  };

  // Spheres for seeker domes, camera balls, sensor gimbals
  const addSphere = (name, subsystem, radius, color, pos, expDelta = { x: 0, y: 0, z: 0 }, metal = 0.3, rough = 0.2, opacity = 1.0, parent = modelGroup) => {
    const geom = new THREE.SphereGeometry(radius, 18, 14);
    return addPart(name, subsystem, geom, makeMat(color, metal, rough, opacity), pos, expDelta, { x: 0, y: 0, z: 0 }, parent);
  };

  // Rings / Torus for bomb fins shrouds, copper armatures
  const addRingZ = (name, subsystem, radius, tube, color, pos, expDelta = { x: 0, y: 0, z: 0 }, metal = 0.5, rough = 0.4, parent = modelGroup) => {
    const geom = new THREE.TorusGeometry(radius, tube, 10, 24);
    return addPart(name, subsystem, geom, makeMat(color, metal, rough), pos, expDelta, { x: 0, y: 0, z: 0 }, parent);
  };

  // Functional Rolling Wheel Assembly
  const addWheel = (name, subsystem, pos, expDelta = { x: 0, y: 0, z: 0 }, radius = 0.32, width = 0.22, isSteerable = false, parent = modelGroup) => {
    const assembly = new THREE.Group();
    assembly.position.set(pos.x, pos.y, pos.z);
    assembly.userData = {
      partName: name,
      subsystem: subsystem,
      basePos: new THREE.Vector3(pos.x, pos.y, pos.z),
      explodedPos: new THREE.Vector3(pos.x + expDelta.x, pos.y + expDelta.y, pos.z + expDelta.z)
    };

    const rollPivot = new THREE.Group();
    assembly.add(rollPivot);
    animWheels.push(rollPivot);

    // Tread tire
    const tireGeom = new THREE.CylinderGeometry(radius, radius, width, 20);
    tireGeom.rotateZ(Math.PI / 2);
    const tireMesh = new THREE.Mesh(tireGeom, makeMat(0x13171d, 0.05, 0.92));
    tireMesh.castShadow = true;
    rollPivot.add(tireMesh);

    // Tire edges
    const tireEdges = new THREE.LineSegments(new THREE.EdgesGeometry(tireGeom, 35), new THREE.LineBasicMaterial({ color: 0x0a0d12, transparent: true, opacity: 0.6 }));
    tireMesh.add(tireEdges);

    // Hub rim
    const hubGeom = new THREE.CylinderGeometry(radius * 0.58, radius * 0.58, width + 0.02, 16);
    hubGeom.rotateZ(Math.PI / 2);
    const hubMesh = new THREE.Mesh(hubGeom, makeMat(0x64748b, 0.75, 0.3));
    rollPivot.add(hubMesh);

    // Center cap
    const capGeom = new THREE.CylinderGeometry(radius * 0.28, radius * 0.28, width + 0.04, 12);
    capGeom.rotateZ(Math.PI / 2);
    const capMesh = new THREE.Mesh(capGeom, makeMat(0x0284c7, 0.6, 0.35));
    rollPivot.add(capMesh);

    if (isSteerable) {
      steerableWheels.push(assembly);
    }

    parent.add(assembly);
    modelParts.push(assembly);
    return assembly;
  };

  const builderHelpers = {
    addPart, addBox, addCylZ, addConeZ, addSphere, addRingZ, addWheel, makeMat,
    modelGroup, modelParts, animRotors, animAfterburners,
    setAnimPropeller: (p) => { animPropeller = p; },
    setAnimTurret: (t) => { animTurret = t; }
  };

  let built = false;
  if (window.VehicleBuilders && window.VehicleBuilders.build) {
    built = window.VehicleBuilders.build(id, builderHelpers);
  }
  if (!built && window.MunitionsBuilders && window.MunitionsBuilders.build) {
    built = window.MunitionsBuilders.build(id, builderHelpers);
  }

  if (!built) {
    addBox('Tactical Ordnance Main Body', 'Armor', 1.0, 1.0, 2.0, 0x334155, { x: 0, y: 0, z: 0 }, { x: 0, y: 0, z: 0 });
    addConeZ('Aerodynamic Guidance Nose Cone', 'Avionics', 0.5, 0.8, 0x0284c7, { x: 0, y: 0, z: 1.4 }, { x: 0, y: 0, z: 0.6 });
    addBox('Rear Stabilizer Fins', 'Airframe', 1.8, 0.04, 0.6, 0x1e293b, { x: 0, y: 0, z: -0.8 }, { x: 0, y: 0, z: -0.4 });
  }

  // Update specs and component list
  renderSpecPanel();
}

// ── UI Rendering & Part Inspection ──────────────────────────────────────────
function renderCategoryNav() {
  const container = document.getElementById('category-nav');
  if (!container || !CATALOG) return;

  container.innerHTML = CATALOG.categories.map(cat => `
    <button class="cat-pill ${cat.id === currentCategory.id ? 'active' : ''}" onclick="selectCategory('${cat.id}')">
      ${cat.name.toUpperCase()} (${cat.items.length})
    </button>
  `).join('');
}

function renderItemsScroller() {
  const container = document.getElementById('items-scroller');
  if (!container || !currentCategory) return;

  container.innerHTML = currentCategory.items.map(item => `
    <div class="model-card ${item.id === currentItem.id ? 'active' : ''}" onclick="selectItem('${item.id}')">
      <div class="card-header-row">
        <span class="card-title">${item.name}</span>
        <span class="card-tag">${item.tier || item.role || item.category}</span>
      </div>
      <p class="card-desc-snippet">${item.desc || ''}</p>
    </div>
  `).join('');
}

function renderSpecPanel() {
  const headline = document.getElementById('stage-title-text');
  const stageMeta = document.getElementById('stage-meta-text');
  if (headline) headline.innerText = currentItem.name;
  if (stageMeta) stageMeta.innerText = (currentItem.role || currentItem.category).toUpperCase();

  const panel = document.getElementById('spec-panel');
  if (!panel || !currentItem) return;

  const specs = [];
  if (currentItem.topSpeed) specs.push({ label: 'Top Speed', val: currentItem.topSpeed });
  if (currentItem.stallSpeed) specs.push({ label: 'Stall Speed', val: currentItem.stallSpeed });
  if (currentItem.health) specs.push({ label: 'Durability', val: currentItem.health });
  if (currentItem.dr) specs.push({ label: 'Armor (DR)', val: currentItem.dr });
  if (currentItem.ap) specs.push({ label: 'Penetration', val: currentItem.ap });
  if (currentItem.blast) specs.push({ label: 'Blast Radius', val: currentItem.blast });
  if (currentItem.range) specs.push({ label: 'Range', val: currentItem.range });
  if (currentItem.rate) specs.push({ label: 'Fire Rate', val: currentItem.rate });
  if (currentItem.fuelRate) specs.push({ label: 'Fuel Burn', val: currentItem.fuelRate });

  const specsHtml = specs.map(s => `
    <div class="tactical-cell">
      <div class="tactical-label">${s.label}</div>
      <div class="tactical-val">${s.val}</div>
    </div>
  `).join('');

  // Major Subcomponents Anatomy
  const uniqueParts = [];
  const seenNames = new Set();
  modelParts.forEach(p => {
    if (p.userData && p.userData.partName && !seenNames.has(p.userData.partName)) {
      seenNames.add(p.userData.partName);
      uniqueParts.push({
        name: p.userData.partName,
        subsystem: p.userData.subsystem || 'HULL'
      });
    }
  });

  const anatomyHtml = `
    <div>
      <div class="spec-box-title">
        <span>Component Breakdown</span>
        <span class="spec-box-badge">${uniqueParts.length} PARTS</span>
      </div>
      <div class="parts-breakdown-list">
        ${uniqueParts.map(p => `
          <div class="part-item-row" onclick="focusAndHighlightPart('${p.name}')">
            <span class="part-name-text">
              <span style="color: var(--accent-cyan); font-size: 8px;">◈</span>
              <span>${p.name}</span>
            </span>
            <span class="part-type-pill type-${(p.subsystem || 'hull').toLowerCase().replace(/\s+/g, '')}">${p.subsystem}</span>
          </div>
        `).join('')}
      </div>
    </div>
  `;

  let weaponsHtml = '';
  if (currentItem.weapons && currentItem.weapons.length > 0) {
    weaponsHtml = `
      <div>
        <div class="spec-box-title">Armament & Hardpoints</div>
        <div class="weapon-chip-list">
          ${currentItem.weapons.map(w => `<div class="weapon-chip">${w}</div>`).join('')}
        </div>
      </div>
    `;
  }

  let craftingHtml = '';
  if (currentItem.recipe) {
    craftingHtml = `
      <div>
        <div class="spec-box-title">In-Game Assembly Recipe</div>
        <div class="prose-card">${currentItem.recipe}</div>
      </div>
    `;
  }

  let controlsHtml = '';
  if (currentItem.controls) {
    controlsHtml = `
      <div>
        <div class="spec-box-title">Tactical Controls</div>
        <div class="prose-card">${currentItem.controls}</div>
      </div>
    `;
  }

  panel.innerHTML = `
    <div class="spec-header-box">
      <h2 class="spec-headline">${currentItem.name}</h2>
      <div class="spec-id-code">ID: ${currentItem.entityId || currentItem.itemId || 'airarsenal:' + currentItem.id}</div>
    </div>

    ${specs.length > 0 ? `<div><div class="spec-box-title">Combat Specifications</div><div class="tactical-grid">${specsHtml}</div></div>` : ''}

    ${anatomyHtml}
    ${weaponsHtml}

    <div>
      <div class="spec-box-title">Tactical Briefing</div>
      <div class="prose-card">${currentItem.desc}</div>
    </div>

    ${craftingHtml}
    ${controlsHtml}

    <div class="tbtechs-manual-quote">
      <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 6px;">
        <div style="display: flex; align-items: center; gap: 6px; color: #f59e0b; font-family: var(--font-display); font-size: 11.5px; font-weight: 700;">
          <span>📖</span>
          <span>TBTECHS OPERATOR DIRECTIVE</span>
        </div>
        <span style="font-family: var(--font-mono); font-size: 10px; background: rgba(245, 158, 11, 0.2); color: #fde047; padding: 1px 6px; border-radius: 4px;">AUTH: TBTECHS</span>
      </div>
      <div style="font-size: 12px; color: #cbd5e1; line-height: 1.5; font-style: italic;">
        "${getTBTechsDirective(currentItem.id)}"
      </div>
      <button class="btn-open-manual-spec" onclick="openManualModal(getManualPageIndexForItem('${currentItem.id}'))">
        <span>📖 OPEN FULL MANUAL CHAPTER</span>
      </button>
    </div>

    <button class="code-inspect-btn" onclick="openCodeModal()">
      <span>VIEW FORGE MODEL & JAVA CODE</span>
    </button>
  `;
}

function focusAndHighlightPart(name) {
  let targetMesh = null;
  for (const part of modelParts) {
    if (part.userData && part.userData.partName === name) {
      targetMesh = part;
      break;
    }
  }

  if (targetMesh) {
    if (hoveredMesh) unhighlightMesh(hoveredMesh);
    hoveredMesh = targetMesh;
    highlightMesh(hoveredMesh);

    const hudName = document.getElementById('part-hud-name');
    const hudSub = document.getElementById('part-hud-subsystem');
    if (hudName) hudName.innerText = name;
    if (hudSub) hudSub.innerText = (targetMesh.userData.subsystem || 'SYSTEM').toUpperCase();

    // Pulse highlight for 2.5s then restore
    setTimeout(() => {
      if (hoveredMesh === targetMesh) {
        unhighlightMesh(hoveredMesh);
        hoveredMesh = null;
      }
    }, 2500);
  }
}

function selectPartItemByName(name) {
  focusAndHighlightPart(name);
}

function selectCategory(catId) {
  currentCategory = CATALOG.categories.find(c => c.id === catId);
  currentItem = currentCategory.items[0];
  renderCategoryNav();
  renderItemsScroller();
  buildModelGeometry(currentItem.id);
  resetCamera();
}

function selectItem(itemId) {
  currentItem = currentCategory.items.find(i => i.id === itemId);
  renderItemsScroller();
  buildModelGeometry(currentItem.id);
}

// Code Inspection Modal
function openCodeModal() {
  const modal = document.getElementById('code-modal');
  if (!modal) return;
  modal.classList.add('open');
  switchModalTab('json');
}

function closeCodeModal() {
  const modal = document.getElementById('code-modal');
  if (modal) modal.classList.remove('open');
}

function switchModalTab(tab) {
  document.querySelectorAll('.modal-tab-btn').forEach(btn => btn.classList.remove('active'));
  const activeBtn = document.getElementById(`tab-${tab}`);
  if (activeBtn) activeBtn.classList.add('active');

  const codeArea = document.getElementById('modal-code-display');
  if (!codeArea || !currentItem) return;

  if (tab === 'json') {
    codeArea.innerText = JSON.stringify({
      "parent": "item/generated",
      "model_type": "forge_1.12.2",
      "id": currentItem.id,
      "registry_name": currentItem.entityId || currentItem.itemId,
      "textures": {
        "layer0": `airarsenal:items/${currentItem.id}`,
        "casing": `airarsenal:textures/models/${currentItem.id}.png`
      },
      "geometry": {
        "scale": [1.0, 1.0, 1.0],
        "render_class": `com.airarsenal.client.renderer.entity.${getRendererClass(currentItem.id)}`
      }
    }, null, 2);
  } else {
    codeArea.innerText = `package com.airarsenal.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * Standard Forge 1.12.2 ModelBase for ${currentItem.name}.
 * Features animated rolling tires, control surfaces, and detailed sub-assemblies.
 */
public class Model${toPascalCase(currentItem.id)} extends ModelBase {

    public ModelRenderer fuselage;
    public ModelRenderer wingAssembly;
    public ModelRenderer landingGearWheelL;
    public ModelRenderer landingGearWheelR;
    public ModelRenderer propulsionUnit;
    public ModelRenderer weaponStation;

    public Model${toPascalCase(currentItem.id)}() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        // Fuselage & Primary Armor
        this.fuselage = new ModelRenderer(this, 0, 0);
        this.fuselage.addBox(-6.0F, -6.0F, -18.0F, 12, 12, 36);
        this.fuselage.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Aerodynamic Wings / Turret
        this.wingAssembly = new ModelRenderer(this, 0, 48);
        this.wingAssembly.addBox(-28.0F, 0.0F, -5.0F, 56, 2, 10);
        this.wingAssembly.setRotationPoint(0.0F, 0.0F, 0.0F);

        // Rolling Rubber Tires
        this.landingGearWheelL = new ModelRenderer(this, 0, 20);
        this.landingGearWheelL.addBox(-2.0F, -4.0F, -4.0F, 4, 8, 8);
        this.landingGearWheelL.setRotationPoint(-8.0F, 8.0F, 2.0F);

        this.landingGearWheelR = new ModelRenderer(this, 0, 20);
        this.landingGearWheelR.addBox(-2.0F, -4.0F, -4.0F, 4, 8, 8);
        this.landingGearWheelR.setRotationPoint(8.0F, 8.0F, 2.0F);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount,
                       float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.fuselage.render(scale);
        this.wingAssembly.render(scale);

        // Animate rolling tires based on forward ground movement
        this.landingGearWheelL.rotateAngleX = limbSwing * 0.8F;
        this.landingGearWheelR.rotateAngleX = limbSwing * 0.8F;
        this.landingGearWheelL.render(scale);
        this.landingGearWheelR.render(scale);
    }
}`;
  }
}

function getRendererClass(id) {
  if (['wood_biplane', 'iron_monoplane', 'fighter_jet', 'stealth_bomber', 'predator_drone', 'attack_helicopter'].includes(id)) {
    return 'PlaneRenderer';
  }
  if (['tank', 'missile_truck', 'armored_truck'].includes(id)) {
    return 'VehicleRenderer';
  }
  if (id.includes('missile') || id === 'hellfire' || id === 'brahmos') {
    return 'MissileRenderer';
  }
  if (id.includes('bomb') || id === 'depth_charge') {
    return 'BombRenderer';
  }
  return 'ShellRenderer';
}

function toPascalCase(str) {
  return str.split('_').map(w => w.charAt(0).toUpperCase() + w.slice(1)).join('');
}

// Initial Data Fetch
window.addEventListener('DOMContentLoaded', async () => {
  try {
    const res = await fetch('/docs/models.json');
    CATALOG = await res.json();
  } catch (err) {
    console.error('Failed loading /docs/models.json', err);
  }

  if (CATALOG && CATALOG.categories) {
    currentCategory = CATALOG.categories[0];
    currentItem = currentCategory.items[0];
    renderCategoryNav();
    renderItemsScroller();
    init3D();
  }
});

// ── TBTechs Field Manual Data & Modal Engine ────────────────────────────────
function getTBTechsDirective(id) {
  const directives = {
    'predator_drone': 'DO NOT attempt to board the cockpit. Stand safely in ground cover, equip the Drone Controller, and right-click the drone. Your optics link instantly via SATCOM. Press Shift (Sneak) to terminate the video feed and leave the UAV on autonomous loiter.',
    'stealth_bomber': 'Engage radar cloaking with Key [G] before entering hostile airspace. Active cloaking blinds automated Flak and SAM batteries, but doubles jet fuel consumption (0.16 units/tick). Monitor fuel closely.',
    'fighter_jet': 'Ensure you maintain at least 45 blocks/sec airspeed. Below stall threshold, lift collapses into an unrecoverable spin. Tap [G] to ignite afterburners for supersonic intercept speed (>160 b/s).',
    'attack_helicopter': 'Operates on collective torque physics. Space climbs, Shift descends. Use [Z] and [C] to execute high-speed sideways strafing runs while keeping your chin-mounted 30mm cannon locked onto ground hostiles.',
    'wood_biplane': 'The ideal primary flight trainer. Hand-prop starts automatically. Keep nose 5 degrees below horizon on landing flare to avoid tip-stalls.',
    'iron_monoplane': 'First enclosed canopy aircraft. Fitted with twin cowl machine guns. Leads targets slightly to account for 120 b/s bullet ballistics.',
    'tank': 'Engineered with 80 Damage Reduction Chobham plating. Operates autonomous auto-turret targeting hostiles within 30 blocks. Fires heavy 120mm sabot tank shells with massive splash damage.',
    'missile_truck': 'Right-click the armored cab to drive. Use W/S for throttle, A/D for 3-degree planetary steering. Upon canister launch, driver camera shifts into the cruise missile nose until detonation.',
    'armored_truck': 'Heavy V-monocoque blast hull (150 HP, 50 DR). Ideal for high-risk troop convoys. Armed with an armored roof cupola and .50 Cal M2HB.',
    'howitzer': 'Open GUI to input target grid coordinates (X, Z). Loads Howitzer High-Explosive shells. Maximum effective range is 800 blocks with parabolic arc.',
    'mortar': 'Set elevation quadrant angle (45° to 85°). Shorter range high-angle plunging fire over trees and fortification walls.',
    'flak_battery': 'Automated perimeter air defense. Detonates proximity flak clouds within 8 blocks of airborne hostile targets. Keep loaded with Flak Shells.',
    'aa_cannon': 'Rapid-fire twin 20mm autocannon. Ideal for low-altitude close-in aircraft interception.',
    'mlrs': 'Fires 12-round ripple salvos of unguided 227mm rockets. Extreme area-denial saturation for enemy fortress destruction.',
    'static_missile_battery': 'Four-cell SAM launcher. Employs radar guidance against detected supersonic aircraft and cruise missiles.',
    'brahmos': 'Mach 3 ramjet cruise missile. Requires designated coordinates via the BrahMos Targeter. Devastating kinetic and blast shockwave.',
    'orbital_cannon': 'Multi-block orbital strike array. Requires full 3x3 Frame + Core structure, Satellite Uplink Card, and Laser Orbital Designator for kinetic rod release.',
    'manpads': 'Shoulder-fired infrared homing missile. Hold right-click to achieve tone lock on airborne jet engines, release to fire.',
    'laser_designator': 'Hold right-click to project an infrared guidance beam onto coordinates. Hellfire and guided missiles will home directly to the painted spot.'
  };

  return directives[id] || 'All operators must consult this field manual before operating heavy machinery or live munitions. Always conduct pre-flight checks, inspect fuel levels, and confirm target sectors.';
}

function getManualPageIndexForItem(id) {
  if (['wood_biplane', 'iron_monoplane', 'fighter_jet'].includes(id)) return 1;
  if (id === 'predator_drone') return 2;
  if (id === 'stealth_bomber') return 3;
  if (id === 'attack_helicopter') return 4;
  if (['tank', 'missile_truck', 'armored_truck'].includes(id)) return 5;
  if (['howitzer', 'mortar', 'flak_battery', 'aa_cannon', 'mlrs', 'static_missile_battery'].includes(id)) return 6;
  if (['brahmos', 'hellfire', 'orbital_cannon', 'orbital_designator', 'laser_designator', 'manpads', 'iron_bomb', 'heavy_bomb', 'napalm_canister'].includes(id)) return 7;
  return 0;
}

const MANUAL_CHAPTERS = [
  { title: 'Foreword', page: 0 },
  { title: 'Flight Mechanics', page: 1 },
  { title: 'Predator UAV', page: 2 },
  { title: 'Stealth Bomber', page: 3 },
  { title: 'Attack Helo', page: 4 },
  { title: 'Combat Vehicles', page: 5 },
  { title: 'Artillery & AA', page: 6 },
  { title: 'Guided Munitions', page: 7 },
  { title: 'Key Cheatsheet', page: 8 }
];

const MANUAL_PAGES = [
  // Page 0: Title & Foreword
  {
    chapter: 'Tactical Directive // Doc #001',
    title: 'TBTechs Operator Field Manual',
    meta: 'Air Arsenal Forge 1.12.2 // Signed by TBTechs // Distribution Restricted',
    content: `
      <div class="page-body-text">
        <strong>ATTENTION OPERATOR:</strong> You are receiving this Field Manual because standard Minecraft survival instincts will not keep you alive behind the stick of a Mach-2 Fighter Jet, a high-altitude B-2 Stealth Bomber, or the remote SATCOM feed of an MQ-1 Predator Drone.
      </div>
      <div class="page-body-text">
        Every vehicle in the Air Arsenal suite is engineered with <em>realistic aerodynamics, authentic fuel burn profiles, armor penetration thresholds, and real flight envelopes</em>. You cannot "guess" your way through a supersonic stall recovery or a laser-guided Hellfire missile launch.
      </div>
      <div class="tbtechs-tip-box">
        <div class="tbtechs-tip-title"><span>★</span> IN-GAME CRAFTING & SPAWN ACCESS</div>
        In Minecraft, this signed Written Book is automatically granted to your inventory upon your first login. You can also craft replacement copies anytime with a <strong>Book & Quill + 1 Redstone Dust</strong> in your crafting grid.
      </div>
      <div class="page-body-text">
        Review the chapters above to master flight controls, remote drone operation, radar cloaking protocols, ground combat vehicles, and heavy artillery.
      </div>
      <div class="tbtechs-signature-block">
        <span>AUTHOR: TBTechs</span>
        <span>SECURITY LEVEL: ALPHA-1 COMMAND</span>
      </div>
    `
  },

  // Page 1: Flight Physics
  {
    chapter: 'Chapter I // Aviation',
    title: 'Fixed-Wing Flight Mechanics & Aerodynamics',
    meta: 'Applies to: Wood Biplane, Iron Monoplane, Fighter Jet, Stealth Bomber',
    content: `
      <div class="page-body-text">
        All fixed-wing aircraft in Air Arsenal generate real physical dynamic lift based on forward airspeed, wing surface area, and angle of attack.
      </div>
      <div class="key-callout-grid">
        <div class="key-callout-card"><span class="kb-pill">W</span><span class="kb-action">Pitch Down (Nose drops)</span></div>
        <div class="key-callout-card"><span class="kb-pill">S</span><span class="kb-action">Pitch Up (Climb / Flare)</span></div>
        <div class="key-callout-card"><span class="kb-pill">A</span><span class="kb-action">Roll / Bank Left</span></div>
        <div class="key-callout-card"><span class="kb-pill">D</span><span class="kb-action">Roll / Bank Right</span></div>
        <div class="key-callout-card"><span class="kb-pill">SPACE</span><span class="kb-action">Throttle Up (Accelerate)</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-SHIFT</span><span class="kb-action">Throttle Down / Wheel Brakes</span></div>
      </div>
      <div class="page-section-heading"><span>⚠️</span> Critical Stall Physics & Recovery</div>
      <div class="page-body-text">
        Every airframe has a strictly enforced <strong>Stall Speed</strong>. If your airspeed drops below this limit (e.g., 45 b/s on the Fighter Jet, 35 b/s on the Stealth Bomber), wings stop generating lift. The aircraft will drop violently. To recover: push <strong>[W]</strong> down to regain forward speed before attempting to pull up.
      </div>
      <div class="tbtechs-tip-box">
        <div class="tbtechs-tip-title"><span>💡</span> TBTECHS FLIGHT PRO-TIP</div>
        Do not land at full speed. Cut throttle with Left-Shift, line up with your runway 200 blocks out, and flare the nose up with [S] just before touchdown to settle onto the main landing gear bogies.
      </div>
    `
  },

  // Page 2: Predator Drone UAV
  {
    chapter: 'Chapter II // Unmanned Systems',
    title: 'MQ-1 Predator Drone Remote Piloting',
    meta: 'System: Predator Drone UAV // Telemetry: Drone Controller SATCOM',
    content: `
      <div class="page-body-text">
        Unlike standard planes, <strong>you do not sit inside the Predator Drone</strong>. True to real military UAV operations, pilots operate from ground control stations.
      </div>
      <div class="page-section-heading"><span>📡</span> Step-by-Step Remote Operation Procedure:</div>
      <div class="page-body-text">
        <strong>1. Craft / Equip the Drone Controller:</strong> Crafted with Iron, Redstone, and an Antenna/Ender Eye.<br>
        <strong>2. Establish SATCOM Link:</strong> Stand near your deployed Predator Drone and <em>Right-Click</em> it with the Drone Controller in hand.<br>
        <strong>3. Visual Telemetry:</strong> Your client screen immediately switches to the drone's high-altitude optical gimbal camera. A custom green FLIR surveillance shader overlay activates.<br>
        <strong>4. Flight Control:</strong> While linked, steering packets transmit remotely. Pitch, yaw, and throttle the UAV using standard flight keys.<br>
        <strong>5. Hellfire Strike:</strong> Left-Click to release underwing laser-guided AGM-114 Hellfire missiles onto targets.<br>
        <strong>6. Disengage Link:</strong> Press <strong>Shift (Sneak)</strong>. Your perspective instantly returns to your player's physical body.
      </div>
      <div class="tbtechs-tip-box">
        <div class="tbtechs-tip-title"><span>🛡️</span> LOITER SURVEILLANCE DOCTRINE</div>
        When you disconnect, the Predator Drone does not crash! It stays server-side on autonomous loiter cruising at its assigned altitude, ready for you to re-link anytime.
      </div>
    `
  },

  // Page 3: Stealth Bomber
  {
    chapter: 'Chapter III // Strategic Aviation',
    title: 'Stealth Bomber (B-2) Radar Evasion & Cloaking',
    meta: 'Role: Strategic Heavy Penetration Bomber // System: Active Radar Cloaking',
    content: `
      <div class="page-body-text">
        The B-2 Stealth Bomber is designed to penetrate heavily defended airspace saturated with automated Flak Batteries and Anti-Aircraft Cannons.
      </div>
      <div class="key-callout-grid">
        <div class="key-callout-card"><span class="kb-pill">G</span><span class="kb-action">Toggle Active Stealth Radar Cloak</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-CLICK</span><span class="kb-action">Release Heavy Bomb Bay Munitions</span></div>
        <div class="key-callout-card"><span class="kb-pill">R-CLICK</span><span class="kb-action">Board / Mount Cockpit Seat</span></div>
      </div>
      <div class="page-section-heading"><span>👻</span> Radar Cloaking Mechanics</div>
      <div class="page-body-text">
        When you press <strong>[G]</strong>, the stealth system activates. In the Forge mod code:
        <br>• Automated <strong>Flak Batteries</strong> and <strong>AA Cannons</strong> will completely lose tracking and cannot fire.
        <br>• Guided missiles lose radar lock.
      </div>
      <div class="tbtechs-tip-box">
        <div class="tbtechs-tip-title"><span>⚠️</span> FUEL CONSUMPTION WARNING</div>
        Active stealth cloaking increases the jet engine fuel burn rate from <strong>0.08 units/tick to 0.16 units/tick</strong>. If your fuel cells drain completely, the cloaking field will instantly collapse, exposing you to enemy fire!
      </div>
    `
  },

  // Page 4: Attack Helicopter
  {
    chapter: 'Chapter IV // Rotary Wing',
    title: 'AH-64 Attack Helicopter & VTOL Hover Dynamics',
    meta: 'Role: Close Air Support // Armament: 30mm Chain Gun & FFAR Rockets',
    content: `
      <div class="page-body-text">
        The Attack Helicopter uses rotary aerofoil physics, allowing vertical takeoff, 360-degree hovering, and precision low-altitude weapon delivery.
      </div>
      <div class="key-callout-grid">
        <div class="key-callout-card"><span class="kb-pill">SPACE</span><span class="kb-action">Increase Collective (Ascend)</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-SHIFT</span><span class="kb-action">Decrease Collective (Descend)</span></div>
        <div class="key-callout-card"><span class="kb-pill">Z</span><span class="kb-action">Lateral Strafe Left</span></div>
        <div class="key-callout-card"><span class="kb-pill">C</span><span class="kb-action">Lateral Strafe Right</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-CLICK</span><span class="kb-action">Fire 30mm Chin Autocannon</span></div>
        <div class="key-callout-card"><span class="kb-pill">G</span><span class="kb-action">Fire Rocket Pod Salvo</span></div>
      </div>
      <div class="tbtechs-tip-box">
        <div class="tbtechs-tip-title"><span>💡</span> HOVER-AND-STRAFE TACTICS</div>
        Release [W] and [S] to enter automated stabilizer hover. While hovering, tap [Z] or [C] to slide sideways out of cover, unleash a rocket barrage, and slide back behind buildings or hills.
      </div>
    `
  },

  // Page 5: Combat Ground Vehicles
  {
    chapter: 'Chapter V // Armor & Ground Warfare',
    title: 'Armored Combat Vehicles & Mobile Launchers',
    meta: 'Units: Main Battle Tank, Missile Command Truck (TEL), Armored Truck (MRAP)',
    content: `
      <div class="page-body-text">
        Ground combat vehicles feature dedicated land physics, terrain step-up assistance, and heavy armor plating.
      </div>
      <div class="page-section-heading"><span>🛡️</span> Main Battle Tank (250 HP, 80 DR)</div>
      <div class="page-body-text">
        Heavy front-line armor. Equipped with Chobham composite plating that shrugs off small arms and light rockets.
        <br>• <strong>Autonomous Target Tracking:</strong> Turret traverses 360 degrees and automatically tracks enemy hostiles within a 30-block sphere.
        <br>• <strong>120mm Smoothbore Cannon:</strong> Fires explosive 120mm Tank Shells every 3 seconds (60 ticks).
      </div>
      <div class="page-section-heading"><span>🚀</span> Missile Command Truck (TEL)</div>
      <div class="page-body-text">
        Right-click to board the driver seat.
        <br>• <strong>Driving:</strong> <strong>W / S</strong> drives forward/reverse; <strong>A / D</strong> steers the front planetary axle (±3°/tick).
        <br>• <strong>Guided Cruise Missile:</strong> Launching transfers your camera directly into the cruise missile's nose cone, enabling full TV steering until explosive impact!
      </div>
      <div class="page-section-heading"><span>🚐</span> Armored Patrol Truck (MRAP)</div>
      <div class="page-body-text">
        150 HP with 50 Damage Reduction. V-monocoque hull designed to absorb landmines and TNT blasts. Mounts an armored roof cupola with a .50 Cal M2HB machine gun.
      </div>
    `
  },

  // Page 6: Artillery & Air Defense
  {
    chapter: 'Chapter VI // Artillery & Air Defense',
    title: 'Artillery Batteries, Mortars & Anti-Air Nets',
    meta: 'Systems: Howitzer, Mortar, MLRS, Flak Battery, AA Cannon',
    content: `
      <div class="page-body-text">
        Artillery installations deliver long-range fire support, while anti-aircraft networks secure your base against airstrikes.
      </div>
      <div class="page-section-heading"><span>🎯</span> Howitzer & Mortar Operation</div>
      <div class="page-body-text">
        • <strong>Howitzer:</strong> Right-click to open the coordinate targeting terminal. Enter target coordinates (X, Z). Loads Howitzer Shells for high-velocity ballistic strikes up to 800 blocks away.<br>
        • <strong>Mortar:</strong> Right-click to adjust elevation angle (45° to 85°). High plunging arc clears mountains and bunker walls.
      </div>
      <div class="page-section-heading"><span>💥</span> Automated Air Defense Nets (Flak & AA)</div>
      <div class="page-body-text">
        • <strong>Flak Battery:</strong> Automatically scans for hostile aircraft up to 60 blocks high. Detonates proximity flak shrapnel that shreds wings and propellers.<br>
        • <strong>Static Missile Battery:</strong> 4-cell SAM installation. Automatically launches surface-to-air interceptors against incoming jets and bombers.
      </div>
    `
  },

  // Page 7: Guided Munitions & Orbital Strike
  {
    chapter: 'Chapter VII // Strategic Weapons',
    title: 'Precision Guided Munitions & Orbital Kinetic Strike',
    meta: 'Weapons: BrahMos Cruise Missile, AGM-114 Hellfire, Orbital Cannon',
    content: `
      <div class="page-body-text">
        High-tier precision armaments for surgical tactical elimination of reinforced enemy fortifications.
      </div>
      <div class="page-section-heading"><span>🚀</span> BrahMos Supersonic Cruise Missile</div>
      <div class="page-body-text">
        Mach 3 ramjet missile. Equip the <strong>BrahMos Targeter</strong>, right-click target coordinates up to 1,000 blocks away, and confirm. The missile launches with an authentic vertical boost before pitching toward the target.
      </div>
      <div class="page-section-heading"><span>⚡</span> Orbital Cannon Kinetic Strike</div>
      <div class="page-body-text">
        Requires constructing the multi-block Orbital Cannon (3x3 Frame + Core).
        <br>1. Insert a configured <strong>Satellite Uplink Card</strong> into the core.
        <br>2. Aim the <strong>Orbital Designator</strong> at any location in the world and hold right-click.
        <br>3. An orbital warning siren sounds as a kinetic tungsten rod descends from orbit, penetrating underground bunkers and creating a devastating shockwave.
      </div>
    `
  },

  // Page 8: Key Cheatsheet
  {
    chapter: 'Reference // Cheatsheet',
    title: 'Master Keybindings & Controls Quick Reference',
    meta: 'All Default Forge 1.12.2 Keybindings registered in Air Arsenal',
    content: `
      <div class="page-body-text">
        Keep this quick reference handy during combat operations:
      </div>
      <div class="key-callout-grid">
        <div class="key-callout-card"><span class="kb-pill">W</span><span class="kb-action">Pitch Down (Planes) // Forward Drive (Truck)</span></div>
        <div class="key-callout-card"><span class="kb-pill">S</span><span class="kb-action">Pitch Up (Planes) // Reverse Drive (Truck)</span></div>
        <div class="key-callout-card"><span class="kb-pill">A / D</span><span class="kb-action">Roll Aircraft // Steer Ground Wheels</span></div>
        <div class="key-callout-card"><span class="kb-pill">SPACE</span><span class="kb-action">Throttle Up // Helicopter Collective Climb</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-SHIFT</span><span class="kb-action">Throttle Down / Brakes // Dismount / Disconnect Drone</span></div>
        <div class="key-callout-card"><span class="kb-pill">G</span><span class="kb-action">Toggle Stealth (B-2) // Afterburner (Jet) // Rockets (Helo)</span></div>
        <div class="key-callout-card"><span class="kb-pill">Z</span><span class="kb-action">Helicopter Strafe Left</span></div>
        <div class="key-callout-card"><span class="kb-pill">C</span><span class="kb-action">Helicopter Strafe Right</span></div>
        <div class="key-callout-card"><span class="kb-pill">L-CLICK</span><span class="kb-action">Fire Primary Machine Guns / Bomb Bay</span></div>
        <div class="key-callout-card"><span class="kb-pill">R-CLICK</span><span class="kb-action">Board Vehicle // Link Drone Controller // Open GUI</span></div>
      </div>
      <div class="tbtechs-signature-block">
        <span>TBTechs Field Manual — Forge 1.12.2</span>
        <span>STATUS: CERTIFIED COMBAT READY</span>
      </div>
    `
  }
];

let currentManualPageIndex = 0;

function openManualModal(pageIndex = 0) {
  currentManualPageIndex = Math.max(0, Math.min(pageIndex, MANUAL_PAGES.length - 1));
  const modal = document.getElementById('manual-modal');
  if (!modal) return;

  renderManualChapterBar();
  renderManualPage();
  modal.classList.add('active');
}

function closeManualModal() {
  const modal = document.getElementById('manual-modal');
  if (modal) modal.classList.remove('active');
}

function renderManualChapterBar() {
  const bar = document.getElementById('manual-chapter-bar');
  if (!bar) return;

  bar.innerHTML = MANUAL_CHAPTERS.map(ch => `
    <button class="chapter-btn ${ch.page === currentManualPageIndex ? 'active' : ''}" onclick="selectManualChapter(${ch.page})">
      ${ch.title}
    </button>
  `).join('');
}

function selectManualChapter(page) {
  currentManualPageIndex = page;
  renderManualChapterBar();
  renderManualPage();
}

function renderManualPage() {
  const page = MANUAL_PAGES[currentManualPageIndex];
  const content = document.getElementById('manual-page-content');
  const counter = document.getElementById('manual-page-counter');
  const btnPrev = document.getElementById('btn-manual-prev');
  const btnNext = document.getElementById('btn-manual-next');

  if (content && page) {
    content.innerHTML = `
      <div class="page-chapter-badge">${page.chapter}</div>
      <h2 class="page-title">${page.title}</h2>
      <div class="page-meta-row">${page.meta}</div>
      ${page.content}
    `;
  }

  if (counter) {
    counter.innerText = `PAGE ${currentManualPageIndex + 1} OF ${MANUAL_PAGES.length}`;
  }

  if (btnPrev) btnPrev.disabled = (currentManualPageIndex === 0);
  if (btnNext) btnNext.disabled = (currentManualPageIndex === MANUAL_PAGES.length - 1);

  renderManualChapterBar();
}

function manualPrevPage() {
  if (currentManualPageIndex > 0) {
    currentManualPageIndex--;
    renderManualPage();
  }
}

function manualNextPage() {
  if (currentManualPageIndex < MANUAL_PAGES.length - 1) {
    currentManualPageIndex++;
    renderManualPage();
  }
}

