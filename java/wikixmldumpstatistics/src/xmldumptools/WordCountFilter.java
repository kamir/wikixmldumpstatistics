package xmldumptools;

import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.WPList;
import info.bliki.wiki.filter.WPTable;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.model.ImageFormat;
import info.bliki.wiki.tags.HTMLTag;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This ITextConverter renders the internal tree node
 * representation as plain text without HTML tags and images.
 *
 */

public class WordCountFilter implements ITextConverter {
  boolean fNoLinks;

  public WordCountFilter(boolean noLinks) {
    this.fNoLinks = noLinks;
  }

  public WordCountFilter() {
    this(true);
  }

  public void nodesToText(List<? extends Object> nodes,
      Appendable resultBuffer, IWikiModel model) throws IOException {
    if (nodes != null && !nodes.isEmpty()) {
      try {
        int level = model.incrementRecursionLevel();

        if (level > Configuration.RENDERER_RECURSION_LIMIT) {
          resultBuffer
              .append("Error - recursion limit exceeded rendering tags in PlainTextConverter#nodesToText().");
          return;
        }
        Iterator<? extends Object> childrenIt = nodes.iterator();
        while (childrenIt.hasNext()) {
          Object item = childrenIt.next();
          if (item != null) {
            if (item instanceof List) {
              nodesToText((List) item, resultBuffer, model);
            } else if (item instanceof ContentToken) {
              ContentToken contentToken = (ContentToken) item;
              String content = contentToken.getContent();
              Utils.escapeXmlToBuffer(content, resultBuffer, true, true, true);
            } else if (item instanceof WPList) {
              ((WPList) item).renderPlainText(this, resultBuffer, model);
            } else if (item instanceof WPTable) {
              ((WPTable) item).renderPlainText(this, resultBuffer, model);
            } else if (item instanceof HTMLTag) {
              ((HTMLTag) item).getBodyString(resultBuffer);
            } else if (item instanceof TagNode) {
              TagNode node = (TagNode) item;
              Map<String, Object> map = node.getObjectAttributes();
              if (map != null && map.size() > 0) {
              } else {
                node.getBodyString(resultBuffer);
              }
            }
          }
        }
      } finally {
        model.decrementRecursionLevel();
      }
    }
  }

  public boolean noLinks() {
    return fNoLinks;
  }

  public void imageNodeToText(TagNode imageTagNode, ImageFormat imageFormat,
      Appendable resultBuffer, IWikiModel model) throws IOException {

  }
}
