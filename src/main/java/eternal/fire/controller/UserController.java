package eternal.fire.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eternal.fire.entity.User;
import eternal.fire.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final String KEY_USER_ID = "__userId__";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User getUserFromSession(HttpSession session) {
        Long id = (Long) session.getAttribute(KEY_USER_ID);
        if (id != null) {
            return userService.getUserById(id);
        }
        return null;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/signin")
    public String signIn(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        model.addAttribute("user", user);
        if (user != null) {
            return "redirect:/profile";
        } else {
            return "signin";
        }
    }

    @PostMapping("/signin")
    public String doSignIn(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.singIn(email, password);
            session.setAttribute(KEY_USER_ID, user.getId());
        } catch (Exception e) {
            log.info("登录失败");
            redirectAttributes.addFlashAttribute("error", "Sign in failed");
            return "redirect:/signin";
        }
        return "redirect:/profile";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return "redirect:/signin";
        } else {
            model.addAttribute("user", user);
            return "profile";
        }
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        User user = getUserFromSession(session);
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam("email") String email, @RequestParam("password") String password, @RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        try {
            log.info("trying to register by {},{},{}", email, password, name);
            User user = userService.register(email, password, name);
        } catch (Exception e) {
            log.info("register failed");
            redirectAttributes.addFlashAttribute("error", "Register failed");
            return "redirect:/register";
        }
        return "redirect:/signin";
    }

    @GetMapping("/signout")
    public String signOut(HttpSession session) {
        session.removeAttribute(KEY_USER_ID);
        return "redirect:/signin";
    }
}
