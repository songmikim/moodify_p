package xyz.moodf.global.codevalue.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import xyz.moodf.global.codevalue.entities.CodeValue;
import xyz.moodf.global.codevalue.entities.QCodeValue;
import xyz.moodf.global.codevalue.repositories.CodeValueRepository;

import java.util.List;

@Lazy
@Service("codeValue")
@RequiredArgsConstructor
public class CodeValueService {

    private final CodeValueRepository repository;
    private final ObjectMapper om;

    public void set(String category, String code, Object value, boolean json) {
        if (json) {
            try {
                value = om.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        repository.saveAndFlush(new CodeValue(code, (String) value, category));
    }

    public void set(String code, Object value, boolean json) {
        set(null, code, value, json);
    }

    /**
     * json을 true로 고정
     *
     * @param code
     * @param value
     */
    public void set(String code, Object value) {
        set(code, value, true);
    }

    public <R> R get(String code, Class<R> clazz) {

        CodeValue item = repository.findById(code).orElse(null);
        if (item != null) {

            // clazz가 JSON 문자열일 때
            if (clazz == String.class) {
                return (R) item.getValue();
            }
            // clazz가 변환된 객체일 때
            else {
                try {
                    return om.readValue(item.getValue(), clazz);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 분류별 전체 목록
     *
     * @param category
     * @return
     */
    public List<CodeValue> getList(String category) {
        QCodeValue codeValue = QCodeValue.codeValue;
        return (List<CodeValue>)repository.findAll(codeValue.category.eq(category));
    }

    public List<CodeValue> getList(List<String> codes) {
        return repository.findAllById(codes);
    }

    public void remove(String code) {
        repository.deleteById(code);
        repository.flush();
    }
}
