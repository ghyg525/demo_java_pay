package org.yangjie.util;

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 二维码工具类
 * @author YangJie [2016年2月18日 下午2:28:14]
 */
public class QrcodeUtil {
	
	/**
	 * 生成二维码
	 * @author YangJie [2016年2月18日 下午2:29:15]
	 * @param content 二维码内容
	 * @param filePath 文件保存路径(相对)
	 * @return
	 * @throws Exception 
	 */
	public static String create(String content, String filePath) throws Exception{
		Path picPath = Paths.get(filePath);
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);	// 容错等级
		hints.put(EncodeHintType.CHARACTER_SET, "utf8");	// 编码
		hints.put(EncodeHintType.MARGIN, 2);	// 白边宽度
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);	// 指定宽度
		MatrixToImageWriter.writeToPath(matrix, "jpg", picPath);	// 生成图片
		return picPath.toUri().toString();
	}
	
	/**
	 * 生成二维码
	 * @author YangJie [2016年2月18日 下午2:29:15]
	 * @param content 二维码内容
	 * @param filePath 文件保存路径(相对)
	 * @return
	 * @throws Exception 
	 */
	public static OutputStream create(String content, OutputStream outputStream) throws Exception{
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);	// 容错等级
		hints.put(EncodeHintType.CHARACTER_SET, "utf8");	// 编码
		hints.put(EncodeHintType.MARGIN, 2);	// 白边宽度
		MultiFormatWriter writer = new MultiFormatWriter();
		BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400, hints);	// 指定宽度
		MatrixToImageWriter.writeToStream(matrix, "jpg", outputStream);	// 生成图片
		return outputStream;
	}

	/**
	 * 解析二维码内容
	 * @author YangJie [2016年2月18日 下午2:29:55]
	 * @param filePath
	 * @return
	 */
	public static String parse(String filePath){
		return null;
	}

}
