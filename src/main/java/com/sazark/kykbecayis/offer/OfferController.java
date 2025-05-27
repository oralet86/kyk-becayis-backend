package com.sazark.kykbecayis.offer;

import com.sazark.kykbecayis.misc.dto.OfferDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @GetMapping
    public ResponseEntity<?> getOffers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long posting,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) String senderUid
    ) {
        if (id != null) {
            OfferDto offer = offerService.findById(id);
            return (offer != null) ? ResponseEntity.ok(offer) : ResponseEntity.notFound().build();
        }

        if (posting != null || senderId != null || senderUid != null) {
            return ResponseEntity.ok(offerService.filterOffers(posting, senderId, senderUid));
        }

        return ResponseEntity.ok(offerService.findAll());
    }

    @PostMapping
    public ResponseEntity<OfferDto> createOffer(@RequestBody OfferDto offerDto) {
        OfferDto savedOffer = offerService.create(offerDto);
        return ResponseEntity
                .created(URI.create("/api/offers/" + savedOffer.getId()))
                .body(savedOffer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable Long id, @RequestBody OfferDto offerDto) {
        OfferDto updatedOffer = offerService.update(id, offerDto);
        return (updatedOffer != null) ? ResponseEntity.ok(updatedOffer) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        boolean deleted = offerService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
