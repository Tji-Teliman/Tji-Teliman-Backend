package com.example.Tji_Teliman.repository;
import com.example.Tji_Teliman.entites.Message;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MessageRepository extends JpaRepository<Message, Long> {
}
