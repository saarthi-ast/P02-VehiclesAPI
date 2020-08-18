package com.udacity.pricing.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
@RequestMapping("${error.path:/error}")
public class ApplicationErrorController implements ErrorController {
    @Value("${error.path:/error}")
    private String errorPath;
    @Override
    public String getErrorPath() {
        return this.errorPath;
    }

    @RequestMapping
    @ResponseBody
    public ResponseEntity<Object> error(HttpServletRequest request) {
        HashMap<String, Object> response = new HashMap<String, Object>();
        response.put("Status","Failure");
        response.put("Message","Operation not supported");
        return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
