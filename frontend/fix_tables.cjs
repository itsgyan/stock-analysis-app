const fs = require('fs');
const path = require('path');
const pagesDir = path.join(__dirname, 'src', 'pages');

const files = fs.readdirSync(pagesDir).filter(f => f.endsWith('.jsx'));

files.forEach(file => {
  const filePath = path.join(pagesDir, file);
  let content = fs.readFileSync(filePath, 'utf8');
  
  if (!content.includes('<table') || content.includes('overflow-x-auto')) {
    return;
  }
  
  content = content.replace(/<table/g, '<div className="overflow-x-auto">\n        <table');
  content = content.replace(/<\/table>/g, '</table>\n      </div>');
  
  fs.writeFileSync(filePath, content, 'utf8');
  console.log('Wrapped tables in ' + file);
});
