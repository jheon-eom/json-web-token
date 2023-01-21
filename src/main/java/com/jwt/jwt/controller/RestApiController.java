package com.jwt.jwt.controller;

import com.jwt.jwt.config.auth.PrincipalDetails;
import com.jwt.jwt.model.Member;
import com.jwt.jwt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class RestApiController {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("join")
    public String join(@RequestBody Member member) {
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        member.setRoles("ROLE_USER");
        memberRepository.save(member);
        return "sign-in";
    }

    @GetMapping("home")
    public String home() {

        return "home";
    }

    @GetMapping("user")
    public String user(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        System.out.println("principal : "+principal.getMember().getId());
        System.out.println("principal : "+principal.getMember().getUsername());
        System.out.println("principal : "+principal.getMember().getPassword());

        return "user";
    }

    @GetMapping("manager/reports")
    public String reports() {
        return "reports";
    }

    @GetMapping("admin/users")
    public List<Member> users(){
        return memberRepository.findAll();
    }

}
