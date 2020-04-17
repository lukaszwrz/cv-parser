package pl.wrzaszcz.cvparser;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import pl.wrzaszcz.cvparser.main.ParserService;

@Controller
public class WelcomeController {

    @Autowired
    private ParserService parserService;

    @GetMapping("/")
    public String main(Model model) {
        return "welcome";
    }

    @PostMapping("/")
    public String mainWithParam(@RequestParam MultipartFile resume, Model model) {

        JSONObject parsedResume = parserService.parseResume(resume);

        model.addAttribute("parsedResume", parsedResume);

        return "welcome"; //view
    }

}