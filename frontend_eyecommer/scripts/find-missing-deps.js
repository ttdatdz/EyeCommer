const fs = require('fs')
const path = require('path')

const root = path.resolve(__dirname, '..')
const pkg = require(path.join(root, 'package.json'))
const deps = Object.assign({}, pkg.dependencies || {}, pkg.devDependencies || {})

function walk(dir, files = []) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name)
    const stat = fs.statSync(p)
    if (stat.isDirectory()) {
      if (name === 'node_modules') continue
      walk(p, files)
    } else if (p.endsWith('.ts') || p.endsWith('.tsx') || p.endsWith('.js') || p.endsWith('.jsx')) {
      files.push(p)
    }
  }
  return files
}

const files = walk(root)
const reImport = /from\s+['\"]([^'\".][^'\"\/]*)['\"]/g
const reRequire = /require\(['\"]([^'\".][^'\"\/]*)['\"]\)/g

const used = new Set()
for (const f of files) {
  const s = fs.readFileSync(f, 'utf8')
  let m
  while ((m = reImport.exec(s))) used.add(m[1])
  while ((m = reRequire.exec(s))) used.add(m[1])
}

const missing = [...used].filter(name => !(name in deps) && !name.startsWith('@/') && !name.startsWith('.'))

console.log('Found', used.size, 'external imports (candidates).')
if (missing.length === 0) {
  console.log('No missing packages found in package.json')
} else {
  console.log('Missing packages:')
  missing.sort().forEach(m => console.log('-', m))
}

// Print a ready-to-run npm install command
if (missing.length) {
  console.log('\nSuggested install command (npm):')
  console.log('npm install --save ' + missing.join(' '))
  console.log('\nOr with yarn:')
  console.log('yarn add ' + missing.join(' '))
}
