package xucheng.media;

import com.j256.simplemagic.ContentInfoUtil;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MinioTests {
    static MinioClient minioClient = new MinioClient
            .Builder()
            .endpoint("http://localhost:9000")
            .credentials("minioadmin", "minioadmin")
            .build();

    //利用ContentInfoUtil通过扩展名获取mimeType
    String mimeType = ContentInfoUtil.findExtensionMatch(".txt").getMimeType();

    @Test
    void testUpload(){
        try {
            minioClient.uploadObject(
                    UploadObjectArgs
                            .builder()
                            .bucket("mediafiles")
                            .object("/test/001.txt")
                            .filename("C:\\Users\\Arvin\\Desktop\\.txt")
                            .contentType(mimeType)
                            .build());
            System.err.println("上传成功");
        } catch (Exception e) {
            System.out.println("上传失败");
        }
    }

    @Test
    void testDownload(){
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs
                            .builder()
                            .bucket("mediafiles")
                            .object(".txt")
                            .filename("C:\\Users\\Arvin\\Desktop\\download.txt")
                            .build());
            System.err.println("下载成功");
        } catch (Exception e) {
            System.out.println("下载失败");
        }
    }

    @Test
    void testRemove(){
        try {
            minioClient.removeObject(
                    RemoveObjectArgs
                            .builder()
                            .bucket("mediafiles")
                            .object(".txt")
                            .build()
            );
            System.out.println("删除成功");
        } catch (Exception e) {
            System.out.println("删除失败");
        }
    }
}
