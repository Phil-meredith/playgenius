package htmlTemplates.components

import kotlinx.html.*

fun BODY.splashHeader(stuff: ()->Unit): Unit =
    header {
        div("container") {
            a(href = "https://playgenius.herokuapp.com/") {
                h1("container--noPad") { +"Play Genius" }
            }
            nav("container__nav") {
                ul("container__nav__list") {
                    li {
                        a(href = "/hello?name=Bob") {
                            h1("container--noPad") { +"Lets Play!" }
                        }
                    }
                    li("container__nav--optional") {
                        a(href = "/") {
                            h1("container--noPad") { +"Who are we?" }
                        }
                    }
                    li("container__nav--optional") {
                        a(href = "/") {
                            h1("container--noPad") { +"Contact" }
                        }
                    }
                }
            }
        }
    }