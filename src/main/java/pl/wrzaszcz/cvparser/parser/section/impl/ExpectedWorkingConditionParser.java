package pl.wrzaszcz.cvparser.parser.section.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import pl.wrzaszcz.cvparser.helpers.Helper;
import pl.wrzaszcz.cvparser.model.Resume;
import pl.wrzaszcz.cvparser.model.Section;
import pl.wrzaszcz.cvparser.parser.SectionParser;
import pl.wrzaszcz.cvparser.processor.Regex;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class ExpectedWorkingConditionParser extends SectionParser {

	private final Regex regex;

	public ExpectedWorkingConditionParser() throws IOException {
		this.regex = new Regex(new ClassPathResource("models/expected_working_condition_patterns.txt").getFile());
	}

	private void handleRegex(Section section, Resume resume) {
		String content = StringUtils.join(section.getContent(), '\n');
		Map<String, List<List<String>>> matches = this.regex.recognise(content);
		for (String entity : matches.keySet()) {
			if (entity.equalsIgnoreCase("ExpectedSalary")) {
				if (!resume.getPersonal().containsKey("ExpectedSalary")) {
					resume.getPersonal().put(entity, matches.get(entity).get(0).get(1));
				}
			}
		}
	}

	@Override
	public void parse(Section section, Resume resume) {
		this.handleRegex(section, resume);
		for (String line : section.getContent()) {
			resume.getExpectedWorkingConditions().add(Helper.removeNonCharacterBegin(line));
		}
	}

}
