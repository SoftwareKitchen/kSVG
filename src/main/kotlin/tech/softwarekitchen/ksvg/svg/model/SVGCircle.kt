package tech.softwarekitchen.ksvg.svg.model

import org.w3c.dom.Element
import tech.softwarekitchen.ksvg.svg.SVGItem

class SVGCircle(source: Element): SVGItem {
    val center: Pair<Double, Double>
    val radius: Double

    init{
        center = Pair(
            source.attributes.getNamedItem("cx")!!.textContent.toDouble(),
            source.attributes.getNamedItem("cy")!!.textContent.toDouble()
        )
        radius = source.attributes.getNamedItem("r")!!.textContent.toDouble()

    }
}
