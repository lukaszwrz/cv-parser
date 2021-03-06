package pl.wrzaszcz.cvparser.parser.section.impl;

import pl.wrzaszcz.cvparser.helpers.Helper;
import pl.wrzaszcz.cvparser.model.Resume;
import pl.wrzaszcz.cvparser.model.Section;
import pl.wrzaszcz.cvparser.parser.SectionParser;

public final class InterestsParser extends SectionParser {

	@Override
	public void parse(Section section, Resume resume) {
		for (String line : section.getContent()) {
			resume.getInterests().add(Helper.removeNonCharacterBegin(line));
		}
	}

}
