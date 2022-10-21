package simbot.example.entity;

import lombok.Data;

import java.io.Serializable;

//ai绘图的接口请求参数
@Data
public class AutoSavePluginConfig implements Serializable {

    //"stable-diffusion-webui接口"
    public String stableDiffusionWebui = "http://127.0.0.1:7860";

    //接口id
    public Integer textFnIndex = 13;

    //接口id
    public Integer imageFnIndex = 33;

    //模式，仅限image2image
    public Integer mode = 0;

    //[暂时不清楚该填什么]原始图蒙版模式，仅限image2image
    public Integer initImgWithMask = -1;

    //[暂时不清楚该填什么]原始图图层，仅限image2image
    public Integer initImgInPaint = -1;

    //[暂时不清楚该填什么]原始蒙版图层，仅限image2image
    public Integer initMaskInPaint = -1;

    //蒙版模式，仅限image2image,[Upload mask|Draw mask]
    public String maskMode = "Draw mask";

    //模糊遮罩滤镜，仅限image2image
    public Integer maskBlur = 4;


    //修复填充，仅限image2image，[fill|original|latent noise|latent nothing]
    public String inPaintingFill = "original";


    //尺寸调整模式，仅限image2image [Crop and resize|Just resize|Resize and fill]
    public String resizeMode = "Just resize";


    //图层全分辨率模式，仅限image2image
    public Boolean inPaintFullRes = false;

    //图层全分辨率填充，仅限image2image
    public Integer inPaintFullResPadding = 32;


    //图层模板倒置，仅限image2image,[Inpaint not masked|Inpaint masked]
    public String inPaintingMaskInvert = "Inpaint masked";


    //翻译词条
    public Boolean translated = true;


    //过滤词条
    public String negativePrompt = "multiple breasts, bad anatomy, liquid body, liquid tongue, disfigured, mutated, anatomical nonsense, text font ui, error, malformed hands, long neck, blurred, lowers,bad anatomy, bad proportions, bad shadow, uncoordinated body, unnatural body, fused breasts, bad breasts, huge breasts, poorly drawn breasts, extra breasts, liquid breasts, missing breasts, huge haunch, huge thighs, huge calf, bad hands, fused hand, missing hand, disappearing arms, disappearing thigh, disappearing calf, disappearing legs, fused ears, bad ears, poorly drawn ears, extra ears, liquid ears, heavy ears, missing ears, fused animal ears, bad animal ears, poorly drawn animal ears, extra animal ears, liquid animal ears, heavy animal earsblurry, JPEG artifacts, signature, 3D, 3D game, 3D game scene, 3D character, malformed feet, extra feet, bad feet, poorly drawn feet, fused feet, missing feet,extra shoes, bad shoes, fused shoes, more than two shoes, poorly drawn shoes, bad gloves, poorly drawn gloves, fused gloves, bad cum, poorly drawn cum, fused cum, bad hairs, poorly drawn hairs, fused hairs,bad eyes, fused eyes poorly drawn eyes, extra eyes, malformed limbs, more than 2 nipples, missing nipples, different nipples, fused nipples, bad nipples, poorly drawn nipples, black nipples, colorful nipples, gross proportions. short arm, (((missing arms))), missing thighs, missing calf, missing legs, mutation, duplicate, morbid, mutilated, poorly drawn hands, more than 1 left hand, more than 1 right hand, deformed, (blurry), disfigured, missing legs, extra arms, extra thighs, more than 2 thighs, extra calf, fused calf, extra legs, bad knee, extra knee, more than 2 legs, bad tails, bad mouth, fused mouth, poorly drawn mouth, bad tongue, tongue within mouth, too long tongue, black tongue,big mouth, cracked mouth, bad mouth, dirty face, dirty teeth, dirty pantie, fused pantie, poorly drawn pantie, fused cloth, poorly drawn cloth, bad pantie, yellow teeth, thick lips, bad cameltoe, colorful cameltoe, bad asshole, poorly drawn asshole, fused asshole, missing asshole, bad anus, bad pussy, bad crotch, bad crotch seam, fused anus, fused pussy, fused anus, fused crotch, poorly drawn crotch, fused seam, poorly drawn anus, poorly drawn pussy, poorly drawn crotch, poorly drawn crotch seam, bad thigh gap, missing thigh gap, fused thigh gap, liquid thigh gap, poorly drawn thigh gap, poorly drawn anus, bad collarbone, fused collarbone, missing collarbone, liquid collarbone, strong girl, obesity, worst quality, low quality, normal quality, liquid tentacles, bad tentacles, poorly drawn tentacles, split tentacles, fused tentacles";


    //词条风格
    public String promptStyle = "None";


    //词条风格2
    public String promptStyle2 = "None";

    //步骤
    public Integer steps = 15;

    //采样器索引,Euler a|Euler|LMS|Heun|DPM2|DPM2 a|DPM fast|DPM adaptive|LMS Karras|DPM2 Karras|DPM2 a Karras|DDIM|PLMS
    // 图片转图片[Euler a|Euler|LMS|Heun|DPM2|DPM2 a|LMS Karras|DPM2 Karras|DPM2 a Karras|DDIM]
    public String samplerIndex = "Euler a";


    //恢复面
    public Boolean restoreFaces = false;


    //生成一个可以平铺的图像
    public Boolean tiling = false;


    //尝试运行多少次
    public Integer nIter = 1;

    //在单个批处理中创建多少图像
    public Integer batchSize = 1;


    //分类器自由引导尺度——图像与提示符的一致程度——越低的值产生越有创意的结果
    public Float cfgScale = 7f;


    //随机种子，-1为随机 只限数字
    public Long seed = -1L;


    //候补种子 只限数字
    public Integer subSeed = -1;


    //候补种子强度
    public Float subSeedStrength = 0f;


    //种子从高调大小
    public Integer seedResizeFromH = 0;


    //种子从宽调大小
    public Integer seedResizeFromW = 0;


    //种子启用额外部分
    public Boolean seedEnableExtras = false;

    //高度
    public Integer height = 512;

    //高度
    public Integer width = 448;


    //Highres.fix 渲染两次
    public Boolean enableHr = false;

    //潜在的尺度
    public Boolean scaleLatent = false;


    //与原图的差异度
    public Float denoisingStrength = 0.7f;


    //脚本,Prompt matrix|Prompts from file or textbox|X/Y plot
    public String script = "None";

    //只有脚本选择X/Y plot后使用
    public String xtype = "Seed";


    //只有脚本选择X/Y plot后使用，否则默认为\"\"
    public String xvalues = "";


    //只有脚本选择X/Y plot后使用，否则默认为\"\"
    public String ytype = "Nothing";

    //只有脚本选择X/Y plot后使用，否则默认为\"\"
    public String yvalues = "";


    //画的传说？搞不懂
    public Boolean drawLegend = true;


    //保持随机
    public Boolean keepRandomSeeds = false;


    //在词条的开始处放置可变部件
    public Boolean putVariablePartsAtStartOfPrompt = false;

}
