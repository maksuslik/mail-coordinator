package me.maksuslik.coordinator.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;

@AllArgsConstructor
@Getter
public class DecodedMessage {
    String emailAddress;
    BigInteger historyId;
}
