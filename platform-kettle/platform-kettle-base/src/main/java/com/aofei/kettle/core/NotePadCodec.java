package com.aofei.kettle.core;

import java.util.Map;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.utils.ColorUtils;
import com.aofei.kettle.utils.StringEscapeHelper;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.NotePadMeta;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;

public class NotePadCodec {

	public static Element encode(NotePadMeta ni) {
		Document doc = mxUtils.createDocument();
		Element note = doc.createElement(PropsUI.NOTEPAD);
		note.setAttribute("label", StringEscapeHelper.encode(ni.getNote()));

		//填充色
		int r = ni.getBackGroundColorRed();
		int g = ni.getBackGroundColorGreen();
		int b = ni.getBackGroundColorBlue();
		note.setAttribute("bgR", String.valueOf(r));
		note.setAttribute("bgG", String.valueOf(g));
		note.setAttribute("bgB", String.valueOf(b));

		//字体颜色
		r = ni.getFontColorRed();
		g = ni.getFontColorGreen();
		b = ni.getFontColorBlue();
		note.setAttribute("fR", String.valueOf(r));
		note.setAttribute("fG", String.valueOf(g));
		note.setAttribute("fB", String.valueOf(b));

		//边框颜色
		r = ni.getBorderColorRed();
		g = ni.getBorderColorGreen();
		b = ni.getBorderColorBlue();
		note.setAttribute("bR", String.valueOf(r));
		note.setAttribute("bG", String.valueOf(g));
		note.setAttribute("bB", String.valueOf(b));

		//字体
		if (!StringUtils.isEmpty(ni.getFontName())) {
			note.setAttribute("fontName", ni.getFontName());
		}

		if (ni.getFontSize() > 0) {
			note.setAttribute("fontSize", String.valueOf(ni.getFontSize()));
		}

		note.setAttribute("fontBold", ni.isFontBold() ? "Y" : "N");
		note.setAttribute("fontItalic", ni.isFontItalic() ? "Y" : "N");
		note.setAttribute("drawShadow", ni.isDrawShadow() ? "Y" : "N");

		return note;
	}

	public static String encodeStyle(NotePadMeta ni) {
		String style = "shape=note;align=left;verticalAlign=top";

		//填充色
		int r = ni.getBackGroundColorRed();
		int g = ni.getBackGroundColorGreen();
		int b = ni.getBackGroundColorBlue();
		style += ";fillColor=" + ColorUtils.toHex(r, g, b);

		//字体颜色
		r = ni.getFontColorRed();
		g = ni.getFontColorGreen();
		b = ni.getFontColorBlue();
		style += ";fontColor=" + ColorUtils.toHex(r, g, b);

		//边框颜色
		r = ni.getBorderColorRed();
		g = ni.getBorderColorGreen();
		b = ni.getBorderColorBlue();
		style += ";strokeColor=" + ColorUtils.toHex(r, g, b);

		if(ni.isDrawShadow()) {
			style += ";shadow=1";
		}

		//字体
		if (!StringUtils.isEmpty(ni.getFontName())) {
			style += ";fontFamily=" + ni.getFontName();
		}

		if (ni.getFontSize() > 0) {
			style += ";fontSize=" + ni.getFontSize();
		}

		int fontStyle = 0;
		if(ni.isFontBold()) {
			fontStyle = fontStyle | mxConstants.FONT_BOLD;
		}
		if(ni.isFontItalic()) {
			fontStyle = fontStyle | mxConstants.FONT_ITALIC;
		}

		if(fontStyle > 0) {
			style += ";fontStyle=" + fontStyle;
		}

		return style;
	}

	public static NotePadMeta decode(mxGraph graph, mxCell cell) {
		String label = cell.getAttribute("label");
		int x = (int) cell.getGeometry().getX();
		int y = (int) cell.getGeometry().getY();
		int w = (int) cell.getGeometry().getWidth();
		int h = (int) cell.getGeometry().getHeight();

		NotePadMeta notePadMeta = new NotePadMeta(StringEscapeHelper.decode(label), x, y, w, h);


		Map style = graph.getCellStyle(cell);

		if(style.containsKey(mxConstants.STYLE_FONTFAMILY)) {
			String fontFamily = (String) style.get(mxConstants.STYLE_FONTFAMILY);
			notePadMeta.setFontName(fontFamily);
		}
		if(style.containsKey(mxConstants.STYLE_FONTSIZE)) {
			String fontSize = (String) style.get(mxConstants.STYLE_FONTSIZE);
			notePadMeta.setFontSize(Const.toInt(fontSize, 9));
		}

		if(style.containsKey(mxConstants.STYLE_SHADOW)) {
			String shadow = (String) style.get(mxConstants.STYLE_SHADOW);
			int shadow_ = Const.toInt(shadow, 0);
			if(shadow_ == 1)
				notePadMeta.setDrawShadow(true);
			else
				notePadMeta.setDrawShadow(false);
		}

		if(style.containsKey(mxConstants.STYLE_FONTSTYLE)) {
			String fontStyle = (String) style.get(mxConstants.STYLE_FONTSTYLE);
			int fontStyle_ = Const.toInt(fontStyle, 0);

			if((fontStyle_ & mxConstants.FONT_BOLD) != 0) {
				notePadMeta.setFontBold(true);
			}

			if((fontStyle_ & mxConstants.FONT_ITALIC) != 0) {
				notePadMeta.setFontItalic(true);
			}
		}

		if(style.containsKey(mxConstants.STYLE_FONTCOLOR)) {
			String fontColor = (String) style.get(mxConstants.STYLE_FONTCOLOR);
			notePadMeta.setFontColorRed(ColorUtils.r(fontColor));
			notePadMeta.setFontColorGreen(ColorUtils.g(fontColor));
			notePadMeta.setFontColorBlue(ColorUtils.b(fontColor));
		}

		if(style.containsKey(mxConstants.STYLE_FILLCOLOR)) {
			String fillColor = (String) style.get(mxConstants.STYLE_FILLCOLOR);
			notePadMeta.setBackGroundColorRed(ColorUtils.r(fillColor));
			notePadMeta.setBackGroundColorGreen(ColorUtils.g(fillColor));
			notePadMeta.setBackGroundColorBlue(ColorUtils.b(fillColor));
		}

		if(style.containsKey(mxConstants.STYLE_STROKECOLOR)) {
			String borderColor = (String) style.get(mxConstants.STYLE_STROKECOLOR);
			notePadMeta.setBorderColorRed(ColorUtils.r(borderColor));
			notePadMeta.setBorderColorGreen(ColorUtils.g(borderColor));
			notePadMeta.setBorderColorBlue(ColorUtils.b(borderColor));
		}
		return notePadMeta;
	}

}
