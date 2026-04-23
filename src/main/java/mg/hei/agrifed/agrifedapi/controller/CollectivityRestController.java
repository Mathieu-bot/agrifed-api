package mg.hei.agrifed.agrifedapi.controller;

import mg.hei.agrifed.agrifedapi.dto.CollectivityInformationDto;
import mg.hei.agrifed.agrifedapi.dto.CollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.CreateCollectivityDto;
import mg.hei.agrifed.agrifedapi.dto.FinancialAccountDto;
import mg.hei.agrifed.agrifedapi.service.CollectivityService;
import mg.hei.agrifed.agrifedapi.service.CollectivityFinancialAccountService;
import mg.hei.agrifed.agrifedapi.validator.EmptyArrayValidator;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityRestController {

    private final CollectivityService collectivityService;
    private final CollectivityFinancialAccountService financialAccountService;

    public CollectivityRestController(CollectivityService collectivityService,
                               CollectivityFinancialAccountService financialAccountService) {
        this.collectivityService = collectivityService;
        this.financialAccountService = financialAccountService;
    }

    @PostMapping
    public ResponseEntity<List<CollectivityDto>> createCollectivities(
            @RequestBody List<CreateCollectivityDto> collectivities) {
        EmptyArrayValidator.validateNotEmpty(collectivities.toArray(new CreateCollectivityDto[0]), "collectivities");
        List<CollectivityDto> created = collectivityService.createCollectivities(collectivities);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/informations")
    public ResponseEntity<CollectivityDto> assignNameAndNumber(
            @PathVariable String id,
            @RequestBody CollectivityInformationDto dto) {
        CollectivityDto updated = collectivityService.assignNameAndNumber(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<List<FinancialAccountDto>> getFinancialAccounts(
            @PathVariable String id,
            @RequestParam(name = "at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate at) {
        List<FinancialAccountDto> accounts = financialAccountService.getFinancialAccounts(Integer.parseInt(id), at);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectivityDto> getCollectivity(@PathVariable Integer id) {
        return ResponseEntity.ok(collectivityService.getById(id));
    }
}