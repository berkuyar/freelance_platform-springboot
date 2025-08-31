package com.uyarberk.freelance_platformm.service;


import com.uyarberk.freelance_platformm.dto.ChangePasswordDto;
import com.uyarberk.freelance_platformm.dto.PublicUserProfileDto;
import com.uyarberk.freelance_platformm.dto.UpdateProfileRequest;
import com.uyarberk.freelance_platformm.dto.UserProfileDto;
import com.uyarberk.freelance_platformm.dto.UserStatsDto;
import com.uyarberk.freelance_platformm.exception.UserNotFoundException;
import com.uyarberk.freelance_platformm.model.User;
import com.uyarberk.freelance_platformm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

      private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileDto getUserProfile(Long userId) {

          User user =  userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("Kullanıcı bulunamadı"));

          UserProfileDto userProfileDto = new UserProfileDto();

          userProfileDto.setId(user.getId());
          userProfileDto.setName(user.getName());
          userProfileDto.setEmail(user.getEmail());
          userProfileDto.setRole(user.getRole());
          userProfileDto.setCity(user.getCity());
          userProfileDto.setCreatedAt(user.getCreatedAt());
          userProfileDto.setBio(user.getBio());
          userProfileDto.setPhone(user.getPhone());
          userProfileDto.setSkills(user.getSkills());
          userProfileDto.setUsername(user.getUsername());
          userProfileDto.setSurname(user.getSurname());
          return userProfileDto;

      }
        public UserProfileDto updateUserProfile(Long userId, UpdateProfileRequest userProfileDto) {

          User user = userRepository.findById(userId).orElseThrow(()
                  -> new UserNotFoundException("Kullanıcı bulunamadı"));
            user.setName(userProfileDto.getName());
            user.setCity(userProfileDto.getCity());
            user.setPhone(userProfileDto.getPhone());
            user.setSkills(userProfileDto.getSkills());

            user.setSurname(userProfileDto.getSurname());
            user.setBio(userProfileDto.getBio());
            userRepository.save(user);

            UserProfileDto updatedUserProfileDto = new UserProfileDto();
            updatedUserProfileDto.setName(userProfileDto.getName());
            updatedUserProfileDto.setCity(userProfileDto.getCity());
            updatedUserProfileDto.setPhone(userProfileDto.getPhone());
            updatedUserProfileDto.setSkills(userProfileDto.getSkills());
            updatedUserProfileDto.setSurname(userProfileDto.getSurname());
            updatedUserProfileDto.setBio(userProfileDto.getBio());

              return  updatedUserProfileDto;

        }

        public UserStatsDto getUserStats(Long userId) {

          User user  = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("Kullanıcı bulunamadı"));

          Long totalPosts = userRepository.countPostsByUserId(userId);
          Long totalBids = userRepository.countBidsByUserId(userId);
          Long acceptedBids = userRepository.countAcceptedBidsByUserId(userId);
          Long rejectedBids = userRepository.countRejectedBidsByUserId(userId);

          Long pendingBids = totalBids - acceptedBids - rejectedBids;
            Double successRate = totalBids > 0 ?
                    (acceptedBids * 100.0 / totalBids) : 0.0;
            
            return new UserStatsDto(totalPosts, totalBids, acceptedBids, 
                                  rejectedBids, pendingBids, successRate);
        }

        @Transactional
        public void deleteAccount(Long userId) {
          User user = userRepository.findActiveById(userId).orElseThrow(()-> new UserNotFoundException("Kullanıcı bulunamadı"));
          user.softDelete();
          userRepository.save(user);
        }
        @Transactional
        public void restoreAccount(Long userId) {
          User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("Kullanıcı bulunamadı"));
          user.restore();
          userRepository.save(user);

        }
         @Transactional
        public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
          User user =  userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("Kullanıcı bulunamadı"));
          if(!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
               throw new RuntimeException("Eski şifre yanlış");
          }
          if(!changePasswordDto.getNewPassword().equals(changePasswordDto.getConfirmNewPassword())) {
              throw new RuntimeException("Yeni şifreler eşleşmiyor");
          }
              user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
              userRepository.save(user);

          }

    public PublicUserProfileDto getPublicUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));

        PublicUserProfileDto publicUserProfileDto = new PublicUserProfileDto();
        publicUserProfileDto.setId(user.getId());
        publicUserProfileDto.setUsername(user.getUsername());
        publicUserProfileDto.setName(user.getName());
        publicUserProfileDto.setSurname(user.getSurname());
        publicUserProfileDto.setBio(user.getBio());
        publicUserProfileDto.setCity(user.getCity());
        publicUserProfileDto.setSkills(user.getSkills());
        publicUserProfileDto.setRole(user.getRole());
        publicUserProfileDto.setCreatedAt(user.getCreatedAt());

        return publicUserProfileDto;
    }

}
