package pl.wrzaszcz.cvparser.parser.section.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import pl.wrzaszcz.cvparser.model.Resume;
import pl.wrzaszcz.cvparser.model.Section;
import pl.wrzaszcz.cvparser.parser.SectionParser;
import pl.wrzaszcz.cvparser.processor.Regex;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class PersonalParser extends SectionParser {

    private final Regex regex;

    public PersonalParser() throws IOException {
        this.regex = new Regex(new ClassPathResource("models/personal_patterns.txt").getFile());
    }

    private void handleRegex(Section section, Resume resume) {
        String content = StringUtils.join(section.getContent(), '\n');
        Map<String, List<List<String>>> matches = this.regex.recognise(content);
        for (String entity : matches.keySet()) {
            if (entity.equalsIgnoreCase("Email")) {
                List<List<String>> emails = matches.get(entity);
                for (int i = 0; i < emails.size(); ++i) {
                    resume.getPersonal().put(String.format("%s %d", entity, i + 1), emails.get(i).get(1));
                }
            } else if (entity.equalsIgnoreCase("Address")) {
                List<List<String>> addresses = matches.get(entity);
                resume.getPersonal().put(entity,
                        addresses.get(0).get(2) == null ? addresses.get(0).get(1) : addresses.get(0).get(2));
            } else if (entity.contains("FullName")) {
                if (matches.get(entity).get(0).size() == 3) {
                    if (!resume.getPersonal().containsKey("FullName")) {
                        resume.getPersonal().put("FullName", matches.get(entity).get(0).get(1));
                    }
                    resume.getPersonal().put("LastName", matches.get(entity).get(0).get(2));
                    resume.getPersonal().put("FirstName", matches.get(entity).get(0).get(1).replaceAll(String.format("[^\\S\\n]*%s[^\\S\\n]*", matches.get(entity).get(0).get(2)), ""));
                } else {
                    if (!resume.getPersonal().containsKey("FullName")) {
                        resume.getPersonal().put("FullName", matches.get(entity).get(0).get(1));
                    }
                }
            } else {
                resume.getPersonal().put(entity, matches.get(entity).get(0).get(1));
            }
        }
    }

    @Override
    public void parse(Section section, Resume resume) {
        this.handleRegex(section, resume);
    }

}
