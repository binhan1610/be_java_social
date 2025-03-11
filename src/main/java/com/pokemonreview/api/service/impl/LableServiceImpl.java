package com.pokemonreview.api.service.impl;

import com.pokemonreview.api.dto.CreateLableDto;
import com.pokemonreview.api.dto.lable.LableDto;
import com.pokemonreview.api.dto.lable.ResponseLableDto;
import com.pokemonreview.api.models.Lable;
import com.pokemonreview.api.models.UserEntity;
import com.pokemonreview.api.repository.LableRepository;
import com.pokemonreview.api.repository.UserRepository;
import com.pokemonreview.api.service.LableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LableServiceImpl implements LableService {
    private LableRepository lableRepository;
    private UserRepository userRepository;

    @Autowired
    public LableServiceImpl(LableRepository lableRepository,UserRepository userRepository) {
        this.userRepository= userRepository;
        this.lableRepository = lableRepository;
    }

    @Override
    public ResponseLableDto findAllLableByUser(String username, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Lable> lables= lableRepository.findLableByUser_Username(username,pageable);
        List<Lable> listofLables= lables.getContent();
        List<LableDto> content = listofLables.stream().map(l -> mapToDto(l)).collect(Collectors.toList());
        ResponseLableDto responseLableDto = new ResponseLableDto();
        responseLableDto.setContent(content);
        responseLableDto.setPageNo(lables.getNumber());
        responseLableDto.setPageSize(lables.getSize());
        responseLableDto.setTotal(lables.getTotalPages());
        responseLableDto.setTotalPages(lables.getTotalPages());
        return responseLableDto;
    }

//    @Override
//    public void deleteLable(int lable_id) {
//        try {
//            lableRepository.deleteLable(lable_id);
//            System.out.println("Deleted Lable with id: " + lable_id);
//        } catch (Exception e) {
//            // In chi tiết lỗi ra
//            System.err.println("Error occurred while deleting Lable: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//    }

    @Override
    public LableDto createLableByUser(String username, String lable_name)
    {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));

        Lable lable = new Lable();
        lable.setUser(user);
        lable.setLable_name(lable_name);
        Lable newLable = lableRepository.save((lable));
        LableDto lableDto = new LableDto();
        lableDto.setId(newLable.getId());
        lableDto.setLabelName(newLable.getLable_name());
        return lableDto;

    }


    //Function

    private LableDto  mapToDto(Lable lable){
        LableDto lableDto = new LableDto();
        lableDto.setId(lable.getId());
        lableDto.setLabelName(lable.getLable_name());
        return lableDto;
    }


}
