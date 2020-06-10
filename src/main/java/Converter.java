import java.io.*;
import java.lang.reflect.Array;

import org.bytedeco.javacpp.*;
import org.bytedeco.ffmpeg.avcodec.*;
import org.bytedeco.ffmpeg.avformat.*;
import org.bytedeco.ffmpeg.avutil.*;
import org.bytedeco.ffmpeg.swscale.*;
import static org.bytedeco.ffmpeg.global.avcodec.*;
import static org.bytedeco.ffmpeg.global.avformat.*;
import static org.bytedeco.ffmpeg.global.avutil.*;
import static org.bytedeco.ffmpeg.global.swscale.*;


public class Converter {
    //static AVFormatContext inputFormatContext;
    public static void main(String[] args) throws ClassNotFoundException {
        //avformat_alloc_context();
        Class cls = Class.forName("Converter");
        AVFormatContext inputFormatContext = avformat_alloc_context();
        AVFormatContext outputFormatContext = new AVFormatContext();
        AVOutputFormat outputFormat = null;
        String inputPath = String.valueOf(cls.getResource("/input.flv"));
        String outputPath = cls.getResource("/").getPath() +  "output2.mp4";

        int err = avformat_open_input(inputFormatContext, inputPath, null, null);
        if (err < 0) {
            byte[] errormsgBytes = new byte[1024];
            int x = av_strerror(err, errormsgBytes, 1024);
            return;
        }
        err = avformat_find_stream_info(inputFormatContext, (AVDictionary) null);
        if (err < 0) {
            byte[] errormsgBytes = new byte[1024];
            int x = av_strerror(err, errormsgBytes, 1024);
            return;
        }
        int audioIdx = -1;
        int videoIdx = -1;
        AVStream audioStream = null;
        AVStream videoStream = null;
        AVStream outputStream = null;
        for(int i = 0; i < inputFormatContext.nb_streams(); i++) {
            if (inputFormatContext.streams(i).codecpar().codec_type() == AVMEDIA_TYPE_AUDIO ) {
                audioIdx = i;
                audioStream = inputFormatContext.streams(i);
            }
            else if (inputFormatContext.streams(i).codecpar().codec_type() == AVMEDIA_TYPE_VIDEO ) {
                videoIdx = i;
                videoStream = inputFormatContext.streams(i);
            }
        }
        if (audioIdx < 0 || videoIdx < 0 ) {
            System.out.println("Cannot find any stream");
            return;
        }
        AVCodecParameters codecParam = avcodec_parameters_alloc();

        outputFormat = av_guess_format(null, outputPath, null);
        int r = avformat_alloc_output_context2(outputFormatContext,outputFormat,null,outputPath);
        //copy video h264
        AVCodec outputVideoCodec = avcodec_find_encoder(outputFormatContext.oformat().video_codec());

        AVCodec inputVideoCodec = avcodec_find_encoder(videoStream.codecpar().codec_id());
        AVCodecContext inCodecContext = avcodec_alloc_context3(inputVideoCodec);
        AVCodecContext outCodecContext = avcodec_alloc_context3(outputVideoCodec);

        avcodec_parameters_from_context(codecParam,inCodecContext);
        avcodec_parameters_to_context(outCodecContext,codecParam);
        avcodec_parameters_free(codecParam);
        AVStream outputVideoStream = avformat_new_stream(outputFormatContext, outputVideoCodec);
        //transcode audio



        //AVOutputFormat = outputFormatContext.oformat();
        //AVCodec codec = avcodec_find_decoder(videoStream.codecpar().codec_id());
        //outputFormatContext.
        //outputStream = avformat_new_stream (outputFormatContext, codec);

        //copy header
        AVIOContext pb = new AVIOContext(null);
        avio_open(pb, outputPath, AVIO_FLAG_WRITE);
        outputFormatContext.pb(pb);
        avformat_write_header(outputFormatContext, (AVDictionary) null);

        /*AVFormatContext fmt_ctx = new AVFormatContext(null);
        AVPacket pkt = new AVPacket();
        AVCodecContext codec_ctx = avcodec_alloc_context3(null);
        AVCodec codec2 = avcodec_find_decoder(codec_ctx.codec_id());

        avutil_configuration();
        //avcodec_align_dimensions();
        avcodec_version();
        //int i = avcodec_close();


        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", inputPath, "-vcodec", "libx264", outputPath);
        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
