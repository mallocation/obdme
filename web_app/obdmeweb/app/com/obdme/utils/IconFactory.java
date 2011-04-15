package com.obdme.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

import models.obdmedb.User;
import play.Logger;
import play.db.jpa.Blob;
import play.vfs.VirtualFile;
import controllers.Profile;
import controllers.Security;

public class IconFactory {
	
	public static File getVehicleDefaut(int size) {
		switch(size) {
		case 512:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/512.png").getRealFile();
		case 256:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/256.png").getRealFile();
		case 128:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/128.png").getRealFile();
		case 64:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/64.png").getRealFile();
		case 32:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/32.png").getRealFile();
		default:
			return VirtualFile.fromRelativePath("/app/files/icon_factory/default/vehicle/128.png").getRealFile();
		}
	}

	public static HashMap<String, Blob> makeIcons(File photo) {

		HashMap<String, Blob> blobMap = new HashMap<String, Blob>();
		
		blobMap.put("512", processImageToIcon(photo, 512));
		blobMap.put("256", processImageToIcon(photo, 256));
		blobMap.put("128", processImageToIcon(photo, 128));
		blobMap.put("64", processImageToIcon(photo, 64));
		blobMap.put("32", processImageToIcon(photo, 32));

		return blobMap;
	}
	
	private static Blob processImageToIcon(File photo, int size) {
		
		try {
			//Read in the uploaded image
			BufferedImage bufferedImage = ImageIO.read(photo);

			//Make a new image to store the final image in
			BufferedImage resizedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

			//Create a manipulatable graphics object
			Graphics2D imageGraphic = resizedImage.createGraphics();
			imageGraphic.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			imageGraphic.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			imageGraphic.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			imageGraphic.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			imageGraphic.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

			//Draw the input image
			imageGraphic.drawImage(bufferedImage, 1, 1, size-2, size-2, null);

			//Load and draw the icon overall
			BufferedImage bufferedOverlayImage = ImageIO.read(VirtualFile.fromRelativePath("/app/files/icon_factory/overlay/" + size + ".png").getRealFile());

			imageGraphic.drawImage(bufferedOverlayImage, 0, 0, size, size, null);

			//Dispose
			imageGraphic.dispose();

			//Load the alpha mask
			BufferedImage bufferedOverlayMask = ImageIO.read(VirtualFile.fromRelativePath("/app/files/icon_factory/mask/" + size + ".png").getRealFile());
			WritableRaster alphaRaster = bufferedOverlayMask.getAlphaRaster();
			
			//for each mask pixel 
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					//get the current pixel color
					Color currentImageColor = new Color(resizedImage.getRGB(i, j));
					
					//Apply the mask
					resizedImage.setRGB(i, j, new Color(currentImageColor.getRed(), currentImageColor.getGreen(), currentImageColor.getBlue(), 255-alphaRaster.getSample(i, j, 0)).getRGB());
				}
			}

			//Output the image as a Byte Array Output Stream
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(resizedImage, "png", baos);

			//Save the Byte Array Output Stream into the blob
			InputStream imageIS = new ByteArrayInputStream(baos.toByteArray());
			Blob imageBlob = new Blob();
			imageBlob.set(imageIS, "image");
		
			return imageBlob;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
