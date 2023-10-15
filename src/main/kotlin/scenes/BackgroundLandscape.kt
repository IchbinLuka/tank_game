package scenes

import core.gameobjects.GameObject
import ext.*
import gameobjects.Tree
import three.DoubleSide
import three.addons.math.MeshSurfaceSampler
import three.js.*
import util.Log
import util.ResourceManager
import util.objectOf
import kotlin.math.PI

class BackgroundLandscape : GameObject() {
    override val renderable: Object3D = Group()

    val plane = Mesh(
        PlaneGeometry(
            width = 200,
            height = 200
        ).apply {
            computeVertexNormals()
        }, MeshToonMaterial().apply {
            side = DoubleSide
        }).apply {
        position.y = 20.0
        rotation.x = -PI * 0.5
    }

    override suspend fun preloadAssets() {
        super.preloadAssets()

        val landscape = ResourceManager.testLandscape.await().scene.apply {
            traverse {
                it.tryCast<Mesh<*, *>>()?.apply {
                    geometry?.unsafeCast<BufferGeometry>()?.apply {
                        computeVertexNormals()
                        computeBoundingBox()
                        computeBoundingSphere()
                    }
                }
            }
            enableShadows()
            applyCustomMat(MeshToonMaterial().apply {
                color = Color(0x195917)
                side = DoubleSide
            })
            scale.set(Vector3(4, 4, 4))
        }.apply {
            position.y = -50.0
        }

        renderable.add(landscape)

        val tree = ResourceManager.backgroundTree.await().scene
        val leaves = tree.getObjectByName("Leaves")?.tryCast<Mesh<*, *>>()?.geometry
        val trunk = tree.getObjectByName("Trunk")?.tryCast<Mesh<*, *>>()?.geometry

        if (leaves == null || trunk == null) {
            Log.e(TAG, "Failed to load tree geometry")
            return
        }

        val leavesMesh = InstancedMesh(
            leaves,
            MeshToonMaterial().apply {
                color = Color(0x05541a)
                userData.outlineParameters = objectOf {
                    // thickness = 0.1
                    // alpha = 0.5
                    visible = true
                    // keepAlive = true
                }
            },
            TREE_COUNT
        )

        val trunkMesh = InstancedMesh(
            trunk,
            MeshToonMaterial().apply {
                color = Color(Tree.TRUNK_COLOR)
            },
            TREE_COUNT
        )

        var mesh: Mesh<*, *>? = null
        landscape.traverse {
            mesh = it.tryCast<Mesh<*, *>>() ?: mesh
        }

        val sampler = MeshSurfaceSampler(mesh!!).build()

        val scale = Vector3(5, 5, 5)

        for (i in 0..<TREE_COUNT) {
            val position = Vector3()
            val normal = Vector3()

            sampler.sample(position, targetNormal = normal)

            val finalPos = position * 165.161 * 4 + Vector3(0, 38, 0)

            leavesMesh.setMatrixAt(i, Matrix4().apply {
                setPosition(finalPos)
                scale(scale)
            })
            trunkMesh.setMatrixAt(i, Matrix4().apply {
                setPosition(finalPos - Vector3(0, 5.0, 0))
                scale(Vector3(1, 1, 1))
            })

        }
        renderable.add(leavesMesh)
        renderable.add(trunkMesh)
    }

    companion object {
        private const val TREE_COUNT = 5_000
        private const val LANDSCAPE_SIZE = 1000
        private const val LANDSCAPE_RESOLUTION = 200
        private const val LANDSCAPE_HEIGHT = 600
    }
}