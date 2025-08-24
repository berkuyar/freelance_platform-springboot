package com.uyarberk.freelance_platformm.service;

import com.uyarberk.freelance_platformm.dto.BidResponse;
import com.uyarberk.freelance_platformm.dto.CreateBidRequest;
import com.uyarberk.freelance_platformm.exception.PostNotFoundException;
import com.uyarberk.freelance_platformm.model.Bid;
import com.uyarberk.freelance_platformm.model.Post;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.BidRepository;
import com.uyarberk.freelance_platformm.repository.PostRepository;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BidService {
    
    private final BidRepository bidRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final NotificationService notificationService;
    
    public BidResponse createBid(CreateBidRequest request, Long userId) {
        // Post'u bul
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new PostNotFoundException("İlan bulunamadı"));
        
        // Post durumu kontrolü - sadece OPEN post'lara bid verilebilir
        if (post.getStatus() != Post.Status.OPEN) {
            throw new RuntimeException("Bu ilana artık teklif verilemez. İlan durumu: " + post.getStatus());
        }
        
        // Duplicate bid kontrolü - aynı freelancer aynı post'a 2 kez bid veremez
        if (bidRepository.existsByPostIdAndUserId(request.getPostId(), userId)) {
            throw new RuntimeException("Bu ilana zaten teklif verdiniz");
        }
        
        // Kullanıcıyı bul
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        
        // Bid oluştur
        Bid bid = new Bid();
        bid.setMiktar(request.getMiktar());
        bid.setMessage(request.getMessage());
        bid.setPost(post);
        bid.setUser(user);
        
        Bid savedBid = bidRepository.save(bid);
        
        // Bildirim gönder
        notificationService.createBidNotification(post, savedBid);
        
        return convertToResponse(savedBid);
    }
    
    public List<BidResponse> getBidsByPost(Long postId, Long employerId) {
        // Post'u bul ve sahibini kontrol et
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("İlan bulunamadı"));
        
        // Sadece post sahibi bid'leri görebilir
        if (!post.getUser().getId().equals(employerId)) {
            throw new RuntimeException("Bu ilana gelen teklifleri görme yetkiniz yok");
        }
        
        List<Bid> bids = bidRepository.findByPostId(postId);
        List<BidResponse> bidResponses = new ArrayList<>();
        for (Bid bid : bids) {
            BidResponse response = convertToResponse(bid);
            bidResponses.add(response);
        }
        return bidResponses;
    }
    
    public List<BidResponse> getMyBids(Long freelancerId) {
        List<Bid> bids = bidRepository.findByUserId(freelancerId);
        List<BidResponse> bidResponses = new ArrayList<>();
        for (Bid bid : bids) {
            BidResponse response = convertToResponse(bid);
            bidResponses.add(response);
        }
        return bidResponses;
    }
    
    public List<BidResponse> getAllMyPostsBids(Long employerId) {
        List<Bid> bids = bidRepository.findBidsByEmployer(employerId);
        List<BidResponse> bidResponses = new ArrayList<>();
        for (Bid bid : bids) {
            BidResponse response = convertToResponse(bid);
            bidResponses.add(response);
        }
        return bidResponses;
    }
    
    public String acceptBid(Long bidId, Long employerId) {
        // Bid'i bul
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        // Sadece post sahibi bid'i kabul edebilir
        if (!bid.getPost().getUser().getId().equals(employerId)) {
            throw new RuntimeException("Bu teklifi kabul etme yetkiniz yok");
        }
        
        // Bid zaten kabul edilmiş mi kontrol et
        if (bid.getStatus() == Bid.Status.ACCEPTED) {
            throw new RuntimeException("Bu teklif zaten kabul edilmiş");
        }
        
        // Bid'i kabul et
        bid.setStatus(Bid.Status.ACCEPTED);
        
        // Post durumunu IN_PROGRESS yap
        Post post = bid.getPost();
        post.setStatus(Post.Status.IN_PROGRESS);
        postRepository.save(post);
        
        // Bid'i kaydet
        bidRepository.save(bid);
        
        // Kabul bildirimi gönder
        notificationService.createBidAcceptedNotification(bid);
        
        // Aynı post'taki diğer PENDING bid'leri otomatik red et
        List<Bid> otherBids = bidRepository.findByPostIdAndStatus(post.getId(), Bid.Status.PENDING);
        for (Bid otherBid : otherBids) {
            if (!otherBid.getId().equals(bidId)) {
                otherBid.setStatus(Bid.Status.REJECTED);
                bidRepository.save(otherBid);
                // Otomatik red edilenler için de bildirim gönder
                notificationService.createBidRejectedNotification(otherBid);
                log.info("Otomatik red edildi. BidId: {} - Kabul edilen bid: {}", otherBid.getId(), bidId);
            }
        }
        
        // Chat odası oluştur
        try {
            chatService.createChatFromBid(bidId);
            log.info("Chat odası oluşturuldu. BidId: {}", bidId);
        } catch (Exception e) {
            log.error("Chat oluşturulurken hata: {}", e.getMessage());
            // Chat oluşturulamazsa bid kabul işlemini geri alma
            // İsterseniz burada rollback yapabilirsiniz
        }
        
        log.info("Bid kabul edildi. BidId: {}, PostId: {}, FreelancerId: {}", 
                bidId, post.getId(), bid.getUser().getId());
        
        return "Teklif başarıyla kabul edildi ve chat odası oluşturuldu";
    }
    
    public String rejectBid(Long bidId, Long employerId) {
        // Bid'i bul
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Teklif bulunamadı"));
        
        // Sadece post sahibi bid'i red edebilir
        if (!bid.getPost().getUser().getId().equals(employerId)) {
            throw new RuntimeException("Bu teklifi red etme yetkiniz yok");
        }
        
        // Bid zaten red edilmiş mi kontrol et
        if (bid.getStatus() == Bid.Status.REJECTED) {
            throw new RuntimeException("Bu teklif zaten red edilmiş");
        }
        
        // Bid'i red et
        bid.setStatus(Bid.Status.REJECTED);
        bidRepository.save(bid);
        
        // Bildirim gönder
        notificationService.createBidRejectedNotification(bid);
        
        log.info("Bid red edildi. BidId: {}, PostId: {}, FreelancerId: {}", 
                bidId, bid.getPost().getId(), bid.getUser().getId());
        
        return "Teklif başarıyla red edildi";
    }
    
    private BidResponse convertToResponse(Bid bid) {
        BidResponse response = new BidResponse();
        response.setId(bid.getId());
        response.setMiktar(bid.getMiktar());
        response.setMessage(bid.getMessage());
        response.setStatus(bid.getStatus().toString());
        response.setCreatedAt(bid.getCreatedAt());
        
        // Post bilgileri
        response.setPostId(bid.getPost().getId());
        response.setPostTitle(bid.getPost().getTitle());
        
        // Freelancer bilgileri
        response.setFreelancerId(bid.getUser().getId());
        response.setFreelancerUsername(bid.getUser().getUsername());
        response.setFreelancerName(bid.getUser().getName() + " " + bid.getUser().getSurname());
        
        return response;
    }
}
