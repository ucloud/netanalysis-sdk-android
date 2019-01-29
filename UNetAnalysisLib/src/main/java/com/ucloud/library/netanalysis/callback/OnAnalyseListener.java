package com.ucloud.library.netanalysis.callback;

import com.ucloud.library.netanalysis.module.UCAnalysisResult;

/**
 * Created by joshua on 2018/9/19 14:01.
 * Company: UCloud
 * E-mail: joshua.yin@ucloud.cn
 */
public interface OnAnalyseListener {
    /** 网络质量分析结果回调, {@link UCAnalysisResult} */
    void onAnalysed(UCAnalysisResult result);
}
