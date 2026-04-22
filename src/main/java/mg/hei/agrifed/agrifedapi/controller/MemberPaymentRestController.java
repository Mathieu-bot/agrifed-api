// controller/MemberPaymentRestController.java
package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.CreateMemberPaymentDto;
import mg.hei.agrifed.agrifedapi.dto.MemberPaymentDto;
import mg.hei.agrifed.agrifedapi.service.MemberPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members/{id}/payments")
public class MemberPaymentRestController {

    private final MemberPaymentService memberPaymentService;

    public MemberPaymentRestController(MemberPaymentService memberPaymentService) {
        this.memberPaymentService = memberPaymentService;
    }

    @PostMapping
    public ResponseEntity<List<MemberPaymentDto>> createPayments(
            @PathVariable Integer id,
            @RequestBody List<CreateMemberPaymentDto> payments) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(memberPaymentService.createPayments(id, payments));
    }
}