# Blueprint Generator

A minimal, local generator that reads the backend parent pom.xml and the frontend package.json to regenerate:
- docs/Technology_Stack_Blueprint.md
- docs/Technology_Stack_Blueprint.json
- docs/Technology_Stack_Blueprint.yaml

Run locally
```pwsh
cd c:/projects/apex/apex-idp/docs/blueprint-generator
npm install
npm run generate
```

Notes
- Paths are absolute to `c:/projects/apex/...` for simplicity; adjust if your workspace path changes.
- Extend the script to include more metadata (licenses, modules, etc.) as needed.
