/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

package eu.dime.ps.controllers.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.DimensionConstrain;
import com.mortennobel.imagescaling.ResampleOp;

public class ImageUtils {

	public static BufferedImage createSquareThumbnail(InputStream inputStream,
			int size) throws IOException {
		return createSquareThumbnail(ImageIO.read(inputStream), size);
	}

	public static BufferedImage createSquareThumbnail(BufferedImage image,
			int size) throws IOException {
		return rescale(square(image), size);
	}

	public static BufferedImage createThumbnail(InputStream inputStream,
			int maxWidth) throws IOException {
		return createThumbnail(ImageIO.read(inputStream), maxWidth);
	}
	
	public static BufferedImage createThumbnail(BufferedImage image,
			int maxWidth) throws IOException {
		return rescale(image, maxWidth);
	}
	
	public static boolean isSupported(String mimeType) {
		return getFormatForMimetype(mimeType) != null;
	}
	
	public static String getFormatForMimetype(String mimeType) {
		String format = null;
		
		if ("image/bmp".equals(mimeType)) {
			format = "bmp";
		} else if ("image/gif".equals(mimeType)) {
			format = "gif";
		} else if ("image/jpeg".equals(mimeType)) {
			format = "jpg";
		} else if ("image/png".equals(mimeType)
				|| "image/x-png".equals(mimeType)) {
			format = "png";
		} else if ("image/vnd.wap.wbmp".equals(mimeType)) {
			format = "wbmp";
		}
		
		return format;
	}

	private static BufferedImage rescale(BufferedImage image, int maxWidth) {
		int originalWidth = image.getWidth();
		int originalHeight = image.getHeight();
		int height = (maxWidth * originalHeight) / originalWidth;
		DimensionConstrain constrain = DimensionConstrain.createAbsolutionDimension(maxWidth, height);
		ResampleOp resampleOp = new ResampleOp(constrain);
		resampleOp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal); // improve the quality a bit
		return resampleOp.filter(image, null);
	}

	private static BufferedImage square(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int x = 0, y = 0;
		
		if (width > height) { // landscape
			x = (width - height) / 2;
			width = height;
		} else { // portrait or square
			y = (height - width) / 2;
			height = width;
		}

		return image.getSubimage(x, y, width, height);
	}
	
	private static BufferedImage crop(BufferedImage image, int width, int height) {
		int originalWidth = image.getWidth();
		int originalHeight = image.getHeight();
		width = width > originalWidth ? originalWidth : width;
		height = height > originalHeight ? originalHeight : height;
		int x = (originalWidth - width) / 2;
		int y = (originalHeight - height) / 2;
		return image.getSubimage(x, y, width, height);
	}
	
}
