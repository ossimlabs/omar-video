package omar.video

import javax.imageio.ImageIO
import javax.imageio.ImageReadParam
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.Raster

class ThumbnailUtilities
{
  static def createThumbnail(def img, Integer w, Integer h, String format="jpeg")
  {
    String formatLowercase = format.toLowerCase()
    int srcW = img.width
    int srcH = img.height
    int x = 0
    int y = 0
    int tgtW = w
    int tgtH = h

    Double scale = srcW/tgtW
    if ( srcH / scale > tgtH ) {
      scale = srcH/tgtH
    }
    Boolean transparentFlag = (formatLowercase != "jpeg" && 
                               formatLowercase != "jpg")
    BufferedImage thumbnailImg = new BufferedImage(w, h, transparentFlag?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);//img.getType());
    //Adjust target
    if(scale >=1)
    {
        tgtW = srcW/scale
        tgtH = srcH/scale
    }
    else
    {
        tgtW = srcW
        tgtH = srcH
    }

    int cntrW = ( w - tgtW ) / 2
    int cntrH = ( h - tgtH ) / 2
    Graphics2D g = thumbnailImg.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                       RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    g.drawImage(img, cntrW, cntrH, tgtW + cntrW, tgtH + cntrH, 0, 0, srcW, srcH, null);
    g.dispose();

    thumbnailImg
  }
  static def createThumbnail(BufferedImage img, Integer size, String format="jpeg")
  {
    createThumbnail(img, size, size, format);
  }

  static BufferedImage fileToBufferedImage(File inputFile)
  {
    def image = ImageIO.read(inputFile)

    image
  }
}