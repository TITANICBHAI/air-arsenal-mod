#!/usr/bin/env node
/**
 * Air Arsenal - GitHub Actions Watcher & Log Puller
 * Watches the build on GitHub Actions for the latest commit,
 * streams status updates, and if a build fails, pulls and displays the error logs.
 */

import { execSync } from 'child_process';
import fs from 'fs';
import path from 'path';

function getToken() {
  if (process.env.GITHUB_TOKEN) return process.env.GITHUB_TOKEN.trim();
  if (process.env.GITHUB_PERSONAL_ACCESS_TOKEN) return process.env.GITHUB_PERSONAL_ACCESS_TOKEN.trim();
  const tokenFile = path.resolve(process.cwd(), '.token');
  if (fs.existsSync(tokenFile)) {
    return fs.readFileSync(tokenFile, 'utf8').trim();
  }
  return '';
}

const TOKEN = getToken();
const OWNER = 'TITANICBHAI';
const REPO = 'air-arsenal-mod';
const HEADERS = {
  'Authorization': `Bearer ${TOKEN}`,
  'Accept': 'application/vnd.github+json',
  'User-Agent': 'AirArsenal-Actions-Watcher'
};

async function getLatestCommitSha() {
  return execSync('git rev-parse HEAD', { encoding: 'utf8' }).trim();
}

async function fetchJson(url) {
  const res = await fetch(url, { headers: HEADERS });
  if (!res.ok) {
    throw new Error(`HTTP ${res.status} from ${url}: ${await res.text()}`);
  }
  return await res.json();
}

async function fetchText(url) {
  const res = await fetch(url, { headers: HEADERS });
  if (!res.ok) {
    throw new Error(`HTTP ${res.status} from ${url}`);
  }
  return await res.text();
}

async function findRunForCommit(sha, maxRetries = 20) {
  console.log(`🔍 Looking for workflow run for commit ${sha.substring(0, 7)}...`);
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    const data = await fetchJson(`https://api.github.com/repos/${OWNER}/${REPO}/actions/runs?event=push&per_page=10`);
    const run = data.workflow_runs?.find(r => r.head_sha === sha);
    if (run) {
      return run;
    }
    await new Promise(resolve => setTimeout(resolve, 3000));
  }
  return null;
}

async function watchRun(runId) {
  console.log(`⏱️ Watching workflow run #${runId}...`);
  let prevStatus = '';
  const startTime = Date.now();

  while (true) {
    const run = await fetchJson(`https://api.github.com/repos/${OWNER}/${REPO}/actions/runs/${runId}`);
    const elapsed = Math.round((Date.now() - startTime) / 1000);
    const statusLine = `[${elapsed}s] Status: ${run.status} | Conclusion: ${run.conclusion || 'pending'}`;
    
    if (statusLine !== prevStatus) {
      console.log(statusLine);
      prevStatus = statusLine;
    }

    if (run.status === 'completed') {
      return run;
    }

    await new Promise(resolve => setTimeout(resolve, 5000));
  }
}

async function printFailureLogs(runId) {
  console.log(`\n❌ Build failed! Fetching job logs for run #${runId}...`);
  const jobsData = await fetchJson(`https://api.github.com/repos/${OWNER}/${REPO}/actions/runs/${runId}/jobs`);
  
  for (const job of jobsData.jobs || []) {
    console.log(`\nJob: ${job.name} (Conclusion: ${job.conclusion})`);
    if (job.conclusion === 'failure') {
      try {
        const logText = await fetchText(`https://api.github.com/repos/${OWNER}/${REPO}/actions/jobs/${job.id}/logs`);
        const lines = logText.split('\n');
        
        // Find errors in compileJava or Gradle failure
        const errorLines = lines.filter(l => 
          l.includes('error:') || 
          l.includes('FAILURE:') || 
          l.includes('FAILED') || 
          l.includes('Compilation failed') ||
          l.includes('* What went wrong:') ||
          l.includes('Execution failed for task')
        );

        console.log('\n--- Relevant Error Output ---');
        console.log(errorLines.slice(-30).join('\n'));
        console.log('-----------------------------\n');
      } catch (err) {
        console.error(`Could not fetch log for job ${job.id}:`, err.message);
      }
    }
  }
}

async function main() {
  try {
    const sha = await getLatestCommitSha();
    console.log(`Target commit: ${sha}`);
    
    const run = await findRunForCommit(sha);
    if (!run) {
      console.error(`❌ Could not find workflow run for commit ${sha}`);
      process.exit(1);
    }

    console.log(`🔗 Run URL: ${run.html_url}`);
    const completedRun = await watchRun(run.id);

    if (completedRun.conclusion === 'success') {
      console.log(`\n🎉 BUILD IS GREEN! Workflow passed successfully!`);
      const artifacts = await fetchJson(`https://api.github.com/repos/${OWNER}/${REPO}/actions/runs/${run.id}/artifacts`);
      if (artifacts.total_count > 0) {
        console.log(`📦 Artifacts generated:`);
        for (const art of artifacts.artifacts) {
          console.log(` - ${art.name} (${(art.size_in_bytes / 1024 / 1024).toFixed(2)} MB)`);
        }
      }
      process.exit(0);
    } else {
      await printFailureLogs(run.id);
      process.exit(1);
    }
  } catch (err) {
    console.error('Fatal error:', err);
    process.exit(1);
  }
}

main();
