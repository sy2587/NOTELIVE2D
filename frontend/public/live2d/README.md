# Live2D model assets

Place licensed Cubism 4/5 Web assets here:

```text
public/live2d/
|- live2dcubismcore.min.js
`- model/
   |- model3.json
   |- model.moc3
   |- textures/
   |- motions/       (optional)
   `- expressions/   (optional)
```

`live2dcubismcore.min.js` must come from the official **Cubism SDK for Web** download.
The model and all textures, motions, expressions, physics, and pose files must use paths matching `model3.json`.

Custom locations can be configured in `frontend/.env.local`:

```text
VITE_LIVE2D_CORE_URL=/live2d/live2dcubismcore.min.js
VITE_LIVE2D_MODEL_URL=/live2d/model/model3.json
```

Do not commit model or Core files unless their licenses allow redistribution.
