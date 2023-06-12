package tech.softwarekitchen.ksvg.svg.model

import org.w3c.dom.Element
import tech.softwarekitchen.ksvg.svg.SVGItem
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyle
import tech.softwarekitchen.ksvg.svg.model.css.parseSVGStyles

class SVGRect(source: Element): SVGItem {
    val topLeft: Pair<Double, Double>
    val size: Pair<Double, Double>
    val styles: List<SVGStyle>

    init{
        topLeft = Pair(
            source.attributes.getNamedItem("x")!!.textContent.toDouble(),
            source.attributes.getNamedItem("y")!!.textContent.toDouble()
        )
        size = Pair(
            source.attributes.getNamedItem("width")!!.textContent.toDouble(),
            source.attributes.getNamedItem("height")!!.textContent.toDouble()
        )
        styles = source.parseSVGStyles()
    }
}
