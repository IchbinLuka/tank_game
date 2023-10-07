package ui

import ext.s
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.span
import kotlinx.html.style

class LoadingSceneController : UIController() {
    override val mainUIBuilder: UIBuilder = {
        div {
            style = "background-color: black; width: 100%; height: 100%"
            h1 {
                style = """
                    text-align: center; 
                    color: white; 
                    position: absolute; 
                    transform: translate(-50%, -50%); 
                    top: 50%; 
                    left: 50%; 
                    font-size: 100px
                """
                span {
                    s("loading")
                }
                span { +"....................." }
            }
        }
    }
}