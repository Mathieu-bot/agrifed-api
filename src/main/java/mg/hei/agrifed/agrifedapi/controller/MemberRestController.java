package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberDto;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.service.MemberService;
import mg.hei.agrifed.agrifedapi.validator.EmptyArrayValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberRestController {

    private final MemberService memberService;

    public MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<List<MemberDto>> createMembers(@RequestBody List<CreateMemberDto> members) {
        EmptyArrayValidator.validateNotEmpty(members.toArray(new CreateMemberDto[0]), "members");
        List<MemberDto> created = memberService.createMembers(members);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}