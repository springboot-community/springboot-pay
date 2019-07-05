package io.springboot.pay.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import io.springboot.pay.util.GeneralUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author KevinBlandy
 * @mender 王小明
 *
 */
@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private ModelAndView forwardView = null;
    @GetMapping("/index.php")
    public ModelAndView index(HttpServletRequest request) {
        logger.info("访问者IP: {}", GeneralUtils.getClientIP(request));
        ModelAndView modelAndView = new ModelAndView("/index/index");
        return modelAndView;
    }

    /**
     * 默认转发页面
     * @param request
     * @return
     */
    @GetMapping("/")
    public ModelAndView defaultPage(HttpServletRequest request) {
        if(forwardView == null) {
            forwardView = new ModelAndView("forward:" + request.getContextPath() + "/index.php");
        }
        return forwardView;
    }
}
