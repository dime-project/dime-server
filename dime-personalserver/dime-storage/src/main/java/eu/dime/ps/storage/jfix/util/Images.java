/*
    Copyright (C) 2010 maik.jablonski@gmail.com

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package eu.dime.ps.storage.jfix.util;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * Utilities to handle image-operations.
 */
public class Images {

	private static class ScaledImage {
		private File file;
		private int size;

		ScaledImage(File file, int size) {
			this.file = file;
			this.size = size;
		}

		File getFile() {
			return file;
		}

		int getSize() {
			return size;
		}
	}

	private static ConcurrentHashMap<File, ScaledImage> imageCache = new ConcurrentHashMap<File, ScaledImage>();

	/**
	 * Scales given image file with given size (in pixels) and caches the
	 * result.
	 */
	public static File scale(File image, int size) throws IOException {
		ScaledImage scaledImage = imageCache.get(image);
		if (scaledImage == null
				|| scaledImage.getFile().lastModified() < image.lastModified()
				|| scaledImage.getSize() != size) {
			Dimension source = getSize(image);
			Dimension target = scaleDimension(source, new Dimension(size, size));
			imageCache.put(image, new ScaledImage(scale(image, target), size));
		}
		return imageCache.get(image).getFile();
	}

	/**
	 * Scales given image to given dimension.
	 * 
	 * If you want to leave the aspect ratio intact, use
	 * {@link #scaleDimension(sourceDimension, targetDimension)} to calculate an
	 * appropriate dimension.
	 */
	public static File scale(File image, Dimension targetDimension)
			throws IOException {
		OutputStream output = null;
		try {
			File targetFile = File.createTempFile("img-", ".tmp");
			output = new FileOutputStream(targetFile);
			ImageIO.write(
					getScaledInstance(ImageIO.read(image),
							(int) targetDimension.getWidth(),
							(int) targetDimension.getHeight()),
					getFormat(image), output);
			return targetFile;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Returns a down-scaled dimension based on given source dimension which is
	 * scaled by given target dimension while keeping the aspect ratio intact.
	 */
	public static Dimension scaleDimension(Dimension source, Dimension target) {
		double width = 0;
		double height = 0;
		double ratio = source.getWidth() / source.getHeight();
		if (ratio > 1) {
			width = target.getWidth();
			height = target.getWidth() / ratio;
			if (height > target.getHeight()) {
				width = target.getHeight() * ratio;
				height = width / ratio;
			}
		} else {
			width = target.getHeight() * ratio;
			height = target.getHeight();
			if (width > target.getWidth()) {
				height = target.getWidth() / ratio;
				width = height * ratio;
			}
		}
		return new Dimension((int) Math.ceil(width), (int) Math.ceil(height));
	}

	public static File rotate(File image) throws IOException {
		OutputStream output = null;
		try {
			File targetFile = File.createTempFile("img-", ".tmp");
			output = new FileOutputStream(targetFile);
			ImageIO.write(getRotatedInstance(ImageIO.read(image)),
					getFormat(image), output);
			return targetFile;
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Returns format name for given image.
	 */
	public static String getFormat(File image) throws IOException {
		ImageInputStream stream = ImageIO
				.createImageInputStream(new FileInputStream(image));
		Iterator iter = ImageIO.getImageReaders(stream);
		if (!iter.hasNext()) {
			return null;
		}
		ImageReader reader = (ImageReader) iter.next();
		stream.close();
		return reader.getFormatName();
	}

	/**
	 * Returns array with width/height for given image.
	 */
	public static Dimension getSize(File image) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(image);
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		return new Dimension(width, height);
	}

	/**
	 * Returns scaled version of given image for given width and height.
	 */
	public static BufferedImage getScaledInstance(BufferedImage image,
			int width, int height) {
		int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage result = new BufferedImage(width, height, type);
		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(
				image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0,
				0, width, height, null);
		graphics.dispose();
		return result;
	}

	/**
	 * Returns clockwise rotated version of given image.
	 */
	public static BufferedImage getRotatedInstance(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		int type = (image.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		BufferedImage result = new BufferedImage(height, width, type);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				result.setRGB(height - 1 - j, i, image.getRGB(i, j));
			}
		}
		return result;
	}
}
