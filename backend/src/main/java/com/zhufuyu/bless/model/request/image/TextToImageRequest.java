package com.zhufuyu.bless.model.request.image;

/**
 * 文生图请求
 */
public class TextToImageRequest {
    
    /**
     * 提示词
     */
    private String prompt;
    
    /**
     * 反向提示词
     */
    private String negativePrompt;
    
    /**
     * 图像数量，默认为1
     */
    private Integer n = 1;
    
    /**
     * 图像尺寸，格式为"宽度*高度"，如"1024*1024"
     */
    private String size = "1024*1024";
    
    /**
     * 使用的模型名称
     */
    private String model = "flux-merged";
    
    /**
     * 随机种子，用于生成确定性结果
     */
    private Integer seed;
    
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getNegativePrompt() {
        return negativePrompt;
    }

    public void setNegativePrompt(String negativePrompt) {
        this.negativePrompt = negativePrompt;
    }

    public Integer getN() {
        return n;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }
}