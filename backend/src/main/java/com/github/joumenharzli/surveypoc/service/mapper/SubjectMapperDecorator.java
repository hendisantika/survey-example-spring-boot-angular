/*
 * Copyright (C) 2018 Joumen Harzli
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */

package com.github.joumenharzli.surveypoc.service.mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.github.joumenharzli.surveypoc.domain.Question;
import com.github.joumenharzli.surveypoc.domain.Subject;
import com.github.joumenharzli.surveypoc.service.dto.SubjectDto;

/**
 * A decorator for the mapper for {@link Subject} and {@link SubjectDto}
 * for custom methods
 *
 * @author Joumen HARZLI
 */
public abstract class SubjectMapperDecorator implements SubjectMapper {

  @Autowired
  @Qualifier("delegate")
  private SubjectMapper delegate;

  @Autowired
  private QuestionMapper questionMapper;

  @Override
  public List<SubjectDto> questionsToSubjectsDto(List<Question> questions) {
    Assert.notNull(questions, "Cannot map a null list of questions to a list of subject dtos");

    //@formatter:off
    return questions
        .stream()
        /* create a of Map<SubjectDto,List<QuestionDto>> */
        .collect(Collectors.groupingBy(question -> delegate.toDto(question.getSubject()),
                                                   LinkedHashMap::new,
                                                   Collectors.mapping(
                                                       question -> questionMapper.questionToQuestionDto(question), Collectors.toList())))
        .entrySet()
        .stream()
        /* move the questions list in the map to the list in the subject */
        .map(e -> {
          e.getKey().addQuestions(e.getValue());
          return e.getKey();
        })
        .collect(Collectors.toList());
    //@formatter:on
  }

}
