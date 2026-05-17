package com.knowledgeos.content.domain.service;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnalysisResult {
    String title;
    String summary;
    List<String> tags;
}
