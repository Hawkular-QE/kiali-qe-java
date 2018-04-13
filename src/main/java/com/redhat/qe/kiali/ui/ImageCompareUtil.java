package com.redhat.qe.kiali.ui;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.redhat.qe.kiali.ui.ImageCompareResult.STATUS;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Jeeva Kandasamy (jkandasa)
 */

@Slf4j
public class ImageCompareUtil {

    public static ImageCompareResult compareImages(String baseFile, String actualFile) {
        ImageCompareResult result = ImageCompareResult.builder()
                .matchPercentage(-1)
                .status(STATUS.PIXEL_MISMATCH)
                .build();
        float matchPercentage = -1;

        // initial size and full match
        Image baseImage = Toolkit.getDefaultToolkit().getImage(baseFile);
        Image actualImage = Toolkit.getDefaultToolkit().getImage(actualFile);
        try {
            PixelGrabber baseImageGrab = new PixelGrabber(baseImage, 0, 0, -1, -1, false);
            PixelGrabber actualImageGrab = new PixelGrabber(actualImage, 0, 0, -1, -1, false);

            int[] baseImageData = null;
            int[] actualImageData = null;

            if (baseImageGrab.grabPixels()) {
                int width = baseImageGrab.getWidth();
                int height = baseImageGrab.getHeight();
                baseImageData = new int[width * height];
                baseImageData = (int[]) baseImageGrab.getPixels();
            }

            if (actualImageGrab.grabPixels()) {
                int width = actualImageGrab.getWidth();
                int height = actualImageGrab.getHeight();
                actualImageData = new int[width * height];
                actualImageData = (int[]) actualImageGrab.getPixels();
            }

            _logger.debug("image{base:[height:{}, width:{}], actual:[height:{}, width:{}]}",
                    baseImageGrab.getHeight(), baseImageGrab.getWidth(), actualImageGrab.getHeight(),
                    actualImageGrab.getWidth());

            if ((baseImageGrab.getHeight() != actualImageGrab.getHeight())
                    || (baseImageGrab.getWidth() != actualImageGrab.getWidth())) {
                result.setStatus(STATUS.SIZE_MISMATCH);
            } else if (java.util.Arrays.equals(baseImageData, actualImageData)) {
                result.setStatus(STATUS.MATCHED);
            }

            // get match percentage
            int q = 0;
            BufferedImage image1 = ImageIO.read(new File(baseFile));
            int widthOfImage1 = image1.getWidth();
            int heightOfImage1 = image1.getHeight();
            int[][] bufOfImage1 = new int[widthOfImage1][heightOfImage1];

            BufferedImage image2 = ImageIO.read(new File(actualFile));
            int widthOfImage2 = image2.getWidth();
            int heightOfImage2 = image2.getHeight();
            int[][] bufOfImage2 = new int[widthOfImage2][heightOfImage2];

            _logger.debug("Files[base:{}, actual:{}], size:{height[base:{}, actual:{}], width[base:{}, actual:{}]}",
                    baseFile, actualFile, heightOfImage1, heightOfImage2, widthOfImage1, widthOfImage2);

            int smallestWidth = Math.min(widthOfImage1, widthOfImage2);
            int smallestHeight = Math.min(heightOfImage1, heightOfImage2);
            int p = 0;
            //calculation of pixel similarity
            for (int a = 0; a < smallestWidth; a++)
            {
                for (int b = 0; b < smallestHeight; b++)
                {
                    bufOfImage2[a][b] = image2.getRGB(a, b);
                    bufOfImage1[a][b] = image1.getRGB(a, b);
                    if (bufOfImage1[a][b] == bufOfImage2[a][b])
                    {
                        p = p + 1;
                    } else {
                        q = q + 1;
                    }
                }
            }
            //percentage calculation
            matchPercentage = (100 * p) / (smallestWidth * smallestHeight);
            result.setMatchPercentage(matchPercentage);

        } catch (InterruptedException | IOException ex) {
            _logger.error("Exception, ", ex);
        }
        return result;
    }
}
