package xucheng.media;

import com.j256.simplemagic.ContentInfoUtil;
import io.minio.*;
import io.minio.messages.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.util.Map;

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
    void testUpload() {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs
                            .builder()
                            .bucket("video")
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
    void testGetObj() {
        try {
            InputStream object = minioClient.getObject(
                    GetObjectArgs
                            .builder()
                            .bucket("video")
                            .object("test/001.txt")
                            .build());

            if (object != null) {
                System.err.println("下载成功：" + object);
            }else {
                System.out.println("下载失败");
            }
        } catch (Exception e) {
            System.err.println("异常");
        }
    }

    @Test
    void testRemove() {
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

    @Test
    void testGetObjTags() {
        GetObjectTagsArgs getObjectTagsArgs = GetObjectTagsArgs.builder()
                .bucket("mediafiles")
                .object("2023/04/01/pexels-pixabay-35857.jpg")
                .build();
        try {
            Tags tags = minioClient.getObjectTags(getObjectTagsArgs);
            Map<String, String> map = tags.get();
            System.out.println(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
