package tech.softwarekitchen.ksvg.svg.draw

import tech.softwarekitchen.common.vector.Vector2i
import tech.softwarekitchen.ksvg.svg.SVGItem
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyle
import java.awt.Graphics2D

interface SVGPartialDrawer<T: SVGItem> {
    fun draw(it: T, size: Vector2i, target: Graphics2D, coordinateMapper: CoordinateMapper, scaler: Scaler2D, parentStyles: List<SVGStyle>)
}
