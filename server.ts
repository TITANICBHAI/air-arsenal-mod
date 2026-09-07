import http from 'node:http';
import fs from 'node:fs';
import path from 'node:path';

const PORT = 3000;
const HOST = '0.0.0.0';

const MIME_TYPES: Record<string, string> = {
  '.html': 'text/html; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.json': 'application/json; charset=utf-8',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml'
};

function serveFile(res: http.ServerResponse, filePath: string, defaultType = 'text/html; charset=utf-8') {
  fs.readFile(filePath, (err, data) => {
    if (err) {
      res.writeHead(500, { 'Content-Type': 'text/plain' });
      res.end('Internal Server Error');
      return;
    }
    const ext = path.extname(filePath).toLowerCase();
    const contentType = MIME_TYPES[ext] || defaultType;
    res.writeHead(200, {
      'Content-Type': contentType,
      'Content-Length': Buffer.byteLength(data),
      'Cache-Control': 'no-cache'
    });
    res.end(data);
  });
}

const server = http.createServer((req, res) => {
  const parsedUrl = new URL(req.url || '/', `http://${req.headers.host || 'localhost'}`);
  let pathname = parsedUrl.pathname;

  // API endpoints
  if (pathname === '/api/models') {
    const modelsPath = path.join(process.cwd(), 'docs', 'models.json');
    if (fs.existsSync(modelsPath)) {
      serveFile(res, modelsPath, 'application/json; charset=utf-8');
      return;
    }
  }

  if (pathname === '/api/status') {
    const payload = JSON.stringify({
      status: 'operational',
      mod: 'Air Arsenal',
      forgeVersion: '1.12.2',
      port: PORT,
      registeredEntities: 32,
      registeredItems: 15,
      registeredBlocks: 8
    });
    res.writeHead(200, {
      'Content-Type': 'application/json; charset=utf-8',
      'Content-Length': Buffer.byteLength(payload)
    });
    res.end(payload);
    return;
  }

  // Handle root and /docs pathing
  if (pathname === '/' || pathname === '/docs' || pathname === '/docs/') {
    pathname = '/docs/index.html';
  }

  // Check file in docs/ directory or current working directory
  let filePath = path.join(process.cwd(), pathname.startsWith('/docs') ? pathname : path.join('docs', pathname));

  if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
    serveFile(res, filePath);
    return;
  }

  // Fallback to docs/index.html
  const fallbackPath = path.join(process.cwd(), 'docs', 'index.html');
  if (fs.existsSync(fallbackPath)) {
    serveFile(res, fallbackPath);
    return;
  }

  res.writeHead(404, { 'Content-Type': 'text/plain' });
  res.end('Air Arsenal Documentation Not Found');
});

server.listen(PORT, HOST, () => {
  console.log(`Air Arsenal documentation server running on http://${HOST}:${PORT}`);
});
