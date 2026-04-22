// controller/MembershipFeeRestController.java
package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.CreateMembershipFeeDto;
import mg.hei.agrifed.agrifedapi.dto.MembershipFeeDto;
import mg.hei.agrifed.agrifedapi.service.MembershipFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/membershipFees")
public class MembershipFeeRestController {

    private final MembershipFeeService membershipFeeService;

    public MembershipFeeRestController(MembershipFeeService membershipFeeService) {
        this.membershipFeeService = membershipFeeService;
    }

    @GetMapping
    public ResponseEntity<List<MembershipFeeDto>> getMembershipFees(@PathVariable Integer id) {
        return ResponseEntity.ok(membershipFeeService.getByCollectivity(id));
    }

    @PostMapping
    public ResponseEntity<List<MembershipFeeDto>> createMembershipFees(
            @PathVariable Integer id,
            @RequestBody List<CreateMembershipFeeDto> fees) {
        return ResponseEntity.ok(membershipFeeService.createForCollectivity(id, fees));
    }
}