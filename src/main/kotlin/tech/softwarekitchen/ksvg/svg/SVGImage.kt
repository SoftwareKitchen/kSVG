package tech.softwarekitchen.ksvg.svg

import org.w3c.dom.Element
import tech.softwarekitchen.common.vector.Vector2i
import tech.softwarekitchen.ksvg.svg.draw.Scaler2D
import tech.softwarekitchen.ksvg.svg.draw.draw
import tech.softwarekitchen.ksvg.svg.model.SVGCircle
import tech.softwarekitchen.ksvg.svg.model.SVGGroup
import tech.softwarekitchen.ksvg.svg.model.SVGPath
import tech.softwarekitchen.ksvg.svg.model.SVGRect
import tech.softwarekitchen.ksvg.svg.model.css.SVGStyle
import tech.softwarekitchen.ksvg.svg.model.css.parseSVGStyles
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.io.File
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory

interface SVGItem

fun Element.svgIterate(): List<SVGItem>{
    val result = ArrayList<SVGItem?>()
    (0 until childNodes.length).map{
        val node = childNodes.item(it)
        if(node is Element){
            result.add(when(node.tagName){
                "g" -> SVGGroup(node)
                "path" -> SVGPath.fromXMLNode(node)
                "circle" -> SVGCircle(node)
                "defs" -> SVGGroup(node)    //Fixme?
                "rect" -> SVGRect(node)              //TODO
                "clipPath" -> null          //TODO
                "metadata" -> null          //TODO
                else -> throw Exception()
            })
        }
    }
    return result.filterNotNull()
}

class SVGImage(file: File) {
    val basePos: Pair<Double, Double>
    val canvasSize: Pair<Double, Double>
    val data: List<SVGItem>
    val styles: List<SVGStyle>

    init {
        val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val xmlDoc = builder.parse(file.inputStream())
        val svgElem = xmlDoc.documentElement

        styles = svgElem.parseSVGStyles()

        val viewBox = svgElem.attributes.getNamedItem("viewBox")
        if (viewBox != null) {
            val vb = viewBox.textContent
            val viewBase = vb.split(" ")
            basePos = Pair(viewBase[0].toDouble(), viewBase[1].toDouble())
            canvasSize = Pair(viewBase[2].toDouble(), viewBase[3].toDouble())
        } else {
            //Go via width & height

            val wid = (svgElem.attributes.getNamedItem("width").textContent).toInt()
            val hei = (svgElem.attributes.getNamedItem("height").textContent).toInt()
            basePos = Pair(0.0, 0.0)
            canvasSize = Pair(wid.toDouble(), hei.toDouble())
        }

        data = svgElem.svgIterate()
    }

    fun getCoordinateMapper(size: Vector2i): (Double, Double) -> Pair<Double, Double>{
        return {
                x, y ->
            Pair(
                size.x * (x - basePos.first) / canvasSize.first,
                size.y * (y - basePos.second) / canvasSize.second
            )
        }
    }

    fun getScaler(size: Vector2i): Scaler2D {
        val facX = size.x.toDouble() / canvasSize.first
        val facY = size.y.toDouble() / canvasSize.second
        return Pair({it * facX}, {it * facY})
    }
}

fun main(){
    val snake = File("magiaaron-Cartoon-Flower.svg")
    val svg = SVGImage(snake)
    val img = BufferedImage(600,800,BufferedImage.TYPE_INT_ARGB)
    svg.draw(img)
    ImageIO.write(img, "png", File("foo.png"))
}
