package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberDto;
import mg.hei.agrifed.agrifedapi.dto.CreateMemberPaymentDto;
import mg.hei.agrifed.agrifedapi.dto.MemberDto;
import mg.hei.agrifed.agrifedapi.dto.MemberPaymentDto;
import mg.hei.agrifed.agrifedapi.service.MemberService;
import mg.hei.agrifed.agrifedapi.service.MemberPaymentService;
import mg.hei.agrifed.agrifedapi.validator.EmptyArrayValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberRestController {

    private final MemberService memberService;
    private final MemberPaymentService memberPaymentService;

    public MemberRestController(MemberService memberService, MemberPaymentService memberPaymentService) {
        this.memberService = memberService;
        this.memberPaymentService = memberPaymentService;
    }

    @PostMapping
    public ResponseEntity<List<MemberDto>> createMembers(@RequestBody List<CreateMemberDto> members) {
        EmptyArrayValidator.validateNotEmpty(members.toArray(new CreateMemberDto[0]), "members");
        List<MemberDto> created = memberService.createMembers(members);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<List<MemberPaymentDto>> createPayments(
            @PathVariable String id,
            @RequestBody List<CreateMemberPaymentDto> payments) {
        EmptyArrayValidator.validateNotEmpty(payments.toArray(new CreateMemberPaymentDto[0]), "payments");
        List<MemberPaymentDto> created = memberPaymentService.createPayments(Integer.parseInt(id), payments);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}