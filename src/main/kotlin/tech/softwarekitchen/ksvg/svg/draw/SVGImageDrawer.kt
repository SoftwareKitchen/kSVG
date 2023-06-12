package tech.softwarekitchen.ksvg.svg.draw

import tech.softwarekitchen.common.vector.Vector2i
import tech.softwarekitchen.ksvg.svg.model.SVGCircle
import tech.softwarekitchen.ksvg.svg.model.SVGGroup
import tech.softwarekitchen.ksvg.svg.SVGImage
import tech.softwarekitchen.ksvg.svg.model.SVGPath
import tech.softwarekitchen.ksvg.svg.model.SVGRect
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyle
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyleType
import tech.softwarekitchen.ksvg.svg.model.css.mergeStyles
import tech.softwarekitchen.ksvg.svg.model.css.parseFillColor
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import kotlin.math.roundToInt

typealias CoordinateMapper = (Double, Double) -> Pair<Double, Double>
typealias Scaler2D = Pair<(Double) -> Double, (Double) -> Double>

fun Graphics2D.drawWithSVGStyle(styles: List<SVGStyle>, fillOp: (Graphics2D) -> Unit, strokeOp: (Graphics2D) -> Unit, scaler: Scaler2D){
    val unscaledStrokeWidth = (styles.firstOrNull{it.type == SVGStyleType.StrokeWidth}?.value ?: "1").toDouble()
    val strokeWidth = scaler.first(unscaledStrokeWidth)
    stroke = BasicStroke(strokeWidth.toFloat())

    val fc = styles.firstOrNull { it.type == SVGStyleType.Fill }?.let { parseFillColor(it.value) }

    if (fc != null) {
        color = fc
        fillOp(this)
    }

    val sc = styles.firstOrNull { it.type == SVGStyleType.Stroke }?.let { parseFillColor(it.value) }

    if(sc != null){
        color = sc
        strokeOp(this)
    }
}

fun SVGPath.draw(size: Vector2i, g: Graphics2D, coordinateMapper: CoordinateMapper, scaler: Scaler2D, parentStyles: List<SVGStyle>){
    SVGPathDrawer().draw(this, size, g, coordinateMapper, scaler, parentStyles)
}

fun SVGRect.draw(g: Graphics2D, scaler: Scaler2D, coordinateMapper: CoordinateMapper, parentStyles: List<SVGStyle>){
    val base = coordinateMapper(topLeft.first, topLeft.second)
    val size = Pair(scaler.first(size.first), scaler.second(size.second))
    val effectiveStyles = mergeStyles(parentStyles, styles)
    g.drawWithSVGStyle(
        effectiveStyles,
        {
            it.fillRect(base.first.roundToInt(), base.second.roundToInt(), size.first.roundToInt(), size.second.roundToInt())
        },
        {
            it.drawRect(base.first.roundToInt(), base.second.roundToInt(), size.first.roundToInt(), size.second.roundToInt())
        },
        scaler
    )
}

fun SVGCircle.draw(g: Graphics2D, scaler: Scaler2D, coordinateMapper: CoordinateMapper, parentStyles: List<SVGStyle>){
    val base = coordinateMapper(center.first, center.second)
    val scaledRadius = scaler.first(radius)

    g.drawWithSVGStyle(
        parentStyles,
        {
            it.fillOval(
                (base.first - scaledRadius).roundToInt(),
                (base.second - scaledRadius).roundToInt(),
                (2*scaledRadius).roundToInt(),
                (2*scaledRadius).roundToInt()
            )
        },
        {
            g.drawOval(
                (base.first - scaledRadius).roundToInt(),
                (base.second - scaledRadius).roundToInt(),
                (2*scaledRadius).roundToInt(),
                (2*scaledRadius).roundToInt(),
            )
        },
        scaler
    )
}

fun SVGGroup.draw(size: Vector2i, g: Graphics2D, scaler: Scaler2D, coordinateMapper: CoordinateMapper, parentStyles: List<SVGStyle>) {
    val effectiveStyles = mergeStyles(parentStyles, styles)
    children.forEach {
        when {
            it is SVGGroup -> it.draw(size, g, scaler, coordinateMapper, effectiveStyles)
            it is SVGPath -> it.draw(size, g,coordinateMapper, scaler, effectiveStyles)
            it is SVGCircle -> it.draw(g, scaler, coordinateMapper, effectiveStyles)
            else -> throw Exception()
        }
    }
}


fun SVGImage.draw(target: BufferedImage){
    val size = Vector2i(target.width, target.height)
    val coordinateMapper: (Double, Double) -> Pair<Double, Double> = getCoordinateMapper(size)
    val scaler = getScaler(size)
    val g = target.createGraphics()

    data.forEach {
        when {
            it is SVGGroup -> it.draw(size, g, scaler, coordinateMapper, styles)
            it is SVGPath -> it.draw(size, g, coordinateMapper, scaler, styles)
            it is SVGCircle -> it.draw(g, scaler, coordinateMapper, styles)
            it is SVGRect -> it.draw(g, scaler, coordinateMapper, styles)
            else -> throw Exception()
        }
    }
}
