package pl.wrzaszcz.cvparser.parser.section.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import pl.wrzaszcz.cvparser.helpers.DateHelper;
import pl.wrzaszcz.cvparser.helpers.Helper;
import pl.wrzaszcz.cvparser.model.Period;
import pl.wrzaszcz.cvparser.model.Resume;
import pl.wrzaszcz.cvparser.model.Section;
import pl.wrzaszcz.cvparser.model.Work;
import pl.wrzaszcz.cvparser.parser.SectionParser;
import pl.wrzaszcz.cvparser.processor.Regex;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class WorkParser extends SectionParser {

	private final Regex regex;
	private final DateHelper dateHelper;

	public WorkParser() throws IOException {
		this.regex = new Regex(new ClassPathResource("models/work_patterns.txt").getFile());
		this.dateHelper = new DateHelper(new ClassPathResource("models/date_patterns.txt").getFile());
	}
	
	private String parseEmployer(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("Employer")) {
			return matches.get("Employer").get(0).get(3);
		}
		return null;
	}

	private String parsePositionHeld(String line) {
		Map<String, List<List<String>>> matches = this.regex.recognise(line);
		if (matches.containsKey("PositionHeld")) {
			return matches.get("PositionHeld").get(0).get(1);
		}
		return null;
	}

	private Boolean isUnknown(String line) {
		return StringUtils.isBlank(this.parseEmployer(line)) && this.dateHelper.parseStartAndEndDate(line) == null
				&& StringUtils.isBlank(this.parsePositionHeld(line));
	}

	@Override
	public void parse(Section section, Resume resume) {
		int lineIndex = 0;

		while (lineIndex < section.getContent().size()) {
			String line = section.getContent().get(lineIndex);
			if (!this.isUnknown(line)) {

				Work work = new Work();
				while (lineIndex < section.getContent().size()
						&& !this.isUnknown(section.getContent().get(lineIndex))) {

					line = section.getContent().get(lineIndex);

					String employer = this.parseEmployer(line);
					Period period = this.dateHelper.parseStartAndEndDate(line);
					String positionHeld = this.parsePositionHeld(line);

					if ((employer != null && work.getEmployer() != null)
							|| (period != null && work.getPeriod() != null)
							|| (positionHeld != null && work.getPositionHeld() != null)) {
						break;
					}

					if (work.getEmployer() == null) {
						work.setEmployer(employer);
					}
					if (work.getPeriod() == null) {
						work.setPeriod(period);
					}
					if (work.getPositionHeld() == null) {
						work.setPositionHeld(positionHeld);;
					}

					lineIndex++;
				}

				while (lineIndex < section.getContent().size() && this.isUnknown(section.getContent().get(lineIndex))) {
					line = section.getContent().get(lineIndex);

					work.getDescription().add(Helper.removeNonCharacterBegin(line));

					lineIndex++;
				}

				resume.getWorks().add(work);
			} else {
				lineIndex++;
			}
		}
	}

}
