package pl.wrzaszcz.cvparser.parser.section.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import pl.wrzaszcz.cvparser.helpers.DateHelper;
import pl.wrzaszcz.cvparser.helpers.Helper;
import pl.wrzaszcz.cvparser.model.Education;
import pl.wrzaszcz.cvparser.model.Period;
import pl.wrzaszcz.cvparser.model.Resume;
import pl.wrzaszcz.cvparser.model.Section;
import pl.wrzaszcz.cvparser.parser.SectionParser;
import pl.wrzaszcz.cvparser.processor.Regex;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class EducationParser extends SectionParser {

	private final Regex regex;
	private final DateHelper dateHelper;

	public EducationParser() throws IOException {
		this.regex = new Regex(new ClassPathResource("models/education_patterns.txt").getFile());
		this.dateHelper = new DateHelper(new ClassPathResource("models/date_patterns.txt").getFile());
	}

	private String parseSchool(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("School")) {
			return matches.get("School").get(0).get(1);
		}
		return null;
	}

	private String parseDegree(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("Degree")) {
			return matches.get("Degree").get(0).get(1);
		}
		return null;
	}

	private Boolean isUnknown(String line) {
		return StringUtils.isBlank(this.parseSchool(line)) && this.dateHelper.parseStartAndEndDate(line) == null
				&& StringUtils.isBlank(this.parseDegree(line));
	}

	@Override
	public void parse(Section section, Resume resume) {
		int lineIndex = 0;

		while (lineIndex < section.getContent().size()) {
			String line = section.getContent().get(lineIndex);
			if (!this.isUnknown(line)) {

				Education education = new Education();
				while (lineIndex < section.getContent().size()
						&& !this.isUnknown(section.getContent().get(lineIndex))) {

					line = section.getContent().get(lineIndex);

					String school = this.parseSchool(line);
					Period period = this.dateHelper.parseStartAndEndDate(line);
					String degree = this.parseDegree(line);

					if ((school != null && education.getSchool() != null)
							|| (period != null && education.getPeriod() != null)
							|| (degree != null && education.getDegree() != null)) {
						break;
					}

					if (education.getSchool() == null) {
						education.setSchool(school);
					}
					if (education.getPeriod() == null) {
						education.setPeriod(period);
					}
					if (education.getDegree() == null) {
						education.setDegree(degree);
					}

					lineIndex++;
				}

				while (lineIndex < section.getContent().size() && this.isUnknown(section.getContent().get(lineIndex))) {
					line = section.getContent().get(lineIndex);

					education.getDescription().add(Helper.removeNonCharacterBegin(line));

					lineIndex++;
				}

				resume.getEducations().add(education);
			} else {
				lineIndex++;
			}
		}
	}

}
