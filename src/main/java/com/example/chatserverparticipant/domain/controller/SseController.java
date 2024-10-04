package com.example.chatserverparticipant.domain.controller;

import com.example.chatserverparticipant.domain.dto.UserReadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/participants")
public class SseController {


}
