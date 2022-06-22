package com.github.alexsol.geekregimeapiusers.controllers.v1.usercontroller

import com.github.alexsol.geekregimeapiusers.ApiUsersSourceResolver
import com.github.alexsol.geekregimeapiusers.constants.ExceptionMessageConstants
import com.github.alexsol.geekregimeapiusers.entities.User
import com.github.alexsol.geekregimeapiusers.exceptions.UserNotFoundException
import com.github.alexsol.geekregimeapiusers.objectToJsonString
import io.mockk.every
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
class GetUserById(
    @Autowired mockMvc: MockMvc,
    @Autowired sourceResolver: ApiUsersSourceResolver
) : BaseUserControllerTest(mockMvc, sourceResolver) {
    @Test
    fun givenUserExist_whenGetUserById_thenReturnsUserWithStatus200() {
        val user = User(id = 1, email = "mark@mail.com")
        every { service.findUserById(user.id) } returns user

        mockMvc.perform(MockMvcRequestBuilders.get("${apiV1Path}/${user.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect { result ->
                Assertions.assertEquals(result.response.contentAsString, objectToJsonString(user))
            }
    }

    @Test
    fun givenUserDoesntExist_whenGetUserById_thenReturnsStatus404() {
        val nullUserId = 10
        every { service.findUserById(nullUserId) } returns null

        mockMvc.perform(MockMvcRequestBuilders.get("${apiV1Path}/${nullUserId}"))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect { result ->
                Assertions.assertTrue(result.resolvedException is UserNotFoundException)
            }
            .andExpect { result ->
                MatcherAssert.assertThat(
                    result.resolvedException?.message,
                    CoreMatchers.containsString(ExceptionMessageConstants.USER_NOT_FOUND)
                )
            }
    }
}