const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = process.env.PORT ? Number(process.env.PORT) : 5100;
const PUBLIC_DIR = path.join(__dirname, "public");

const MIME_TYPES = {
  ".html": "text/html",
  ".js": "text/javascript",
  ".css": "text/css",
};

const server = http.createServer((req, res) => {
  const requestedPath = req.url === "/" ? "/index.html" : req.url;
  const filePath = path.join(PUBLIC_DIR, decodeURIComponent(requestedPath.split("?")[0]));

  fs.readFile(filePath, (err, content) => {
    if (err) {
      res.writeHead(404, { "Content-Type": "text/plain" });
      res.end("Not found");
      return;
    }
    const ext = path.extname(filePath);
    res.writeHead(200, { "Content-Type": MIME_TYPES[ext] || "application/octet-stream" });
    res.end(content);
  });
});

server.listen(PORT, () => {
  console.log(`potter-frontend listening on http://localhost:${PORT}`);
});
