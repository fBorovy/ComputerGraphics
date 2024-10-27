package com.fborowy.computergraphics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.filament.Engine
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView


@Composable
fun HSVCone3D(
    engine: Engine,
    hue: Float
) {

    val view = rememberView(engine)

    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environment = rememberEnvironment(engine)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(5.dp))
    ) {
        Scene(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.tertiary),
            engine = engine,
            view = view,
            renderer = renderer,
            scene = scene,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            mainLightNode = rememberMainLightNode(engine) {
                intensity = 100000f
            },
            environment = environment,
            cameraNode = rememberCameraNode(engine) {
                position = Position(x = 0f, y = 0f, z = 2.2f)
            },
            cameraManipulator = null,
            childNodes = rememberNodes {
                add(
                    ModelNode(
                        modelInstance = modelLoader.createModelInstance(
                            assetFileLocation = "models3D/cone.glb"
                        ),
                        scaleToUnits = 1.4f,
                    ).apply {
                        transform(
                            position = Position(y = 0.1f),
                            rotation = Rotation(y = hue)
                        )
                    }
                )
            }
        )
    }
}