package tech.softwarekitchen.ksvg.svg.model

import org.w3c.dom.Element
import tech.softwarekitchen.ksvg.svg.SVGItem
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyle
import tech.softwarekitchen.ksvg.svg.model.css.parseSVGStyles
import tech.softwarekitchen.ksvg.svg.svgIterate

class SVGGroup(data: Element): SVGItem {
    val children: List<SVGItem>
    val styles: List<SVGStyle>

    init{
        children = data.svgIterate()
        styles = data.parseSVGStyles()
    }
}