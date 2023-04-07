package xucheng.media;

import com.xuecheng.base.utils.Mp4VideoUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class FFmpegTests {
    //ffmpeg的路径
    static String ffmpeg_path = "C:\\Develop\\ffmpeg-6.0-essentials_build\\bin\\ffmpeg.exe";//ffmpeg的安装位置
    //源avi视频的路径
    static String video_path = "D:\\Download\\Edge\\others\\test.avi";
    //转换后mp4文件的名称
    static String mp4_name = "test.mp4";
    //转换后mp4文件的路径
    static String mp4_path = "D:\\Download\\Edge\\others\\test.mp4";

    static Mp4VideoUtil mp4VideoUtil;

    @BeforeAll
    static void setMp4VideoUtil(){
        mp4VideoUtil = new Mp4VideoUtil(ffmpeg_path,video_path, mp4_name, mp4_path);
    }

    @Test
    void testMp4VideoUtil(){
        String s = mp4VideoUtil.generateMp4();
        System.err.println(s);
    }

    @Test
    void test(){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(" -i  D:\\Download\\Edge\\others\\");
        processBuilder.redirectErrorStream(true);
        try {
            Process start = processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
